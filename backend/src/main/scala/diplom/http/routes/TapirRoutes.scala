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


class Endpoints[F[_]: Sync: Time: Logger](services: Services[F]) {

  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger

  private val billingClientsEndpoints =
    new BillingClientsEndpoints[F](services.billingClients).endpoints

  val statusEndpoint: PublicEndpoint[Unit, Unit, StatusResponse, Any] = endpoint.get
    .in("status")
    .out(jsonBody[StatusResponse])
    .description("проверка статуса приложения.")

  val statusServerEndpoint: ServerEndpoint[Any, F] =
    statusEndpoint.serverLogicSuccess(transitCallMetricIncoming => StatusResponse("OK", "Сообщение").pure[F])

  val apiEndpoints =
    List(statusServerEndpoint) ++ billingClientsEndpoints

  val docEndpoints: List[ServerEndpoint[Any, F]] =
    SwaggerInterpreter(swaggerUIOptions = SwaggerUIOptions.default.copy(contextPath = List("v1", "api")).withAbsolutePaths)
      .fromServerEndpoints[F](apiEndpoints, "Diplom-NispD", "1.0.0")

  val prometheusMetrics: PrometheusMetrics[F] = PrometheusMetrics.default[F]()
  val metricsEndpoint: ServerEndpoint[Any, F] = prometheusMetrics.metricsEndpoint

  val all: List[ServerEndpoint[Any, F]] = apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)
}

final case class TapirRoutes[F[_]: Monad: Async: Logger](
  services: Services[F]
) extends Http4sDsl[F] {

  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger

  private[routes] val prefixPath = "/api"

  val endoints = new Endpoints(services)

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
