package com.mcn.diplom.http.routes

import cats.Monad
import cats.effect.kernel.{ Async, Sync }
import cats.syntax.all._
import com.mcn.diplom.domain.StatusResponse
import com.mcn.diplom.lib.Time
import com.mcn.diplom.modules.Services
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{ Logger, SelfAwareStructuredLogger }
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.{ Http4sServerInterpreter, Http4sServerOptions }
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import com.mcn.http.routes.admin.BillingClientsEndpoints
import com.mcn.diplom.http.routes.admin.BillingBillingPacketsEndpoints
import com.mcn.diplom.http.routes.admin.BillingServiceNumbersEndpoints
import com.mcn.diplom.http.routes.admin.BillingServiceTrunksEndpoints
import com.mcn.diplom.http.routes.admin.BillingPricelistsEndpoints
import com.mcn.diplom.http.routes.admin.BillingPricelistItemsEndpoints
import com.mcn.diplom.http.routes.admin.AuthTrunksEndpoints
import com.mcn.diplom.http.routes.admin.AuthUsersEndpoints
import com.mcn.diplom.http.routes.admin.CallCdrEndpoints
import com.mcn.diplom.http.routes.admin.CallRawEndpoints
import com.mcn.diplom.http.routes.admin.com.mcn.diplom.http.routes.admin.AccEndpoints
import com.mcn.diplom.modules.Programs

class Endpoints[F[_]: Sync: Time: Logger](services: Services[F], programs: Programs[F]) {

  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger

  private val billingClientsEndpoints =
    new BillingClientsEndpoints[F](services.billingClients).endpoints

  private val billingPacketsEndpoints =
    new BillingBillingPacketsEndpoints[F](services.billingPacketsService).endpoints

  private val billingServiceNumbersEndpoints =
    new BillingServiceNumbersEndpoints[F](services.billingServiceNumbersService).endpoints

  private val billingServiceTrunksEndpoints =
    new BillingServiceTrunksEndpoints[F](services.billingServiceTrunksService).endpoints

  private val billingPricelistsEndpoints =
    new BillingPricelistsEndpoints[F](services.billingPricelistsService).endpoints

  private val billingPricelistItemsEndpoints =
    new BillingPricelistItemsEndpoints[F](services.billingPricelistItemsService).endpoints

  private val authTrunksEndpoints =
    new AuthTrunksEndpoints[F](services.authTrunksService).endpoints

  private val authUsersEndpoints =
    new AuthUsersEndpoints[F](services.authUsersService).endpoints

  private val callCdrEndpoints =
    new CallCdrEndpoints[F](services.callCdrService).endpoints

  private val callRawEndpoints =
    new CallRawEndpoints[F](services.callRawService).endpoints

  private val accEndpoints =
    new AccEndpoints[F](services.callCdrService, programs.billingCall).endpoints

  val statusEndpoint: PublicEndpoint[Unit, Unit, StatusResponse, Any] = endpoint.get
    .in("status")
    .out(jsonBody[StatusResponse])
    .description("проверка статуса приложения.")

  val statusServerEndpoint: ServerEndpoint[Any, F] =
    statusEndpoint.serverLogicSuccess(transitCallMetricIncoming => StatusResponse("OK", "Сообщение").pure[F])

  val apiEndpoints =
    List(
      statusServerEndpoint
    ) ++ billingClientsEndpoints ++
      billingPacketsEndpoints ++
      billingServiceNumbersEndpoints ++
      billingServiceTrunksEndpoints ++
      billingPricelistsEndpoints ++
      billingPricelistItemsEndpoints ++
      authTrunksEndpoints ++
      authUsersEndpoints ++
      callCdrEndpoints ++
      callRawEndpoints ++
      accEndpoints

  val docEndpoints: List[ServerEndpoint[Any, F]] =
    SwaggerInterpreter(swaggerUIOptions = SwaggerUIOptions.default.copy(contextPath = List("v1", "api")).withAbsolutePaths)
      .fromServerEndpoints[F](apiEndpoints, "Diplom-NispD", "1.0.0")

  val prometheusMetrics: PrometheusMetrics[F] = PrometheusMetrics.default[F]()
  val metricsEndpoint: ServerEndpoint[Any, F] = prometheusMetrics.metricsEndpoint

  val all: List[ServerEndpoint[Any, F]] = apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)
}

final case class TapirRoutes[F[_]: Monad: Async: Logger](
  services: Services[F],
  programs: Programs[F]
) extends Http4sDsl[F] {

  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger

  private[routes] val prefixPath = "/api"

  val endoints = new Endpoints(services, programs)

  val serverOptions: Http4sServerOptions[F] =
    Http4sServerOptions
      .customiseInterceptors[F]
      .metricsInterceptor(endoints.prometheusMetrics.metricsInterceptor())
      .options

  private val tapirRoutes = Http4sServerInterpreter[F](serverOptions).toRoutes(endoints.all)

  val routes: HttpRoutes[F] = Router(
    prefixPath -> tapirRoutes
  )

}
