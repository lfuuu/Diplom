package com.mcn.diplom.modules

import cats.effect.Async
import cats.syntax.all._
import com.mcn.diplom.domain.UserError
import com.mcn.diplom.http.auth.users._
import com.mcn.diplom.http.routes._
import com.mcn.diplom.http.routes.secured.SecuredTestRoutes
import com.mcn.diplom.http.{ HttpErrorHandler, UserHttpErrorHandler }
import com.mcn.diplom.resources.AppResources
import com.olegpy.meow.hierarchy._
import dev.profunktor.auth.JwtAuthMiddleware
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._
import org.typelevel.log4cats.Logger

object HttpApi {

  def make[F[_]: Async: Logger](
    services: Services[F],
    programs: Programs[F],
    appResources: AppResources[F],
    security: Security[F]
  ): HttpApi[F] =
    new HttpApi[F](services, programs, appResources, security) {}
}

sealed abstract class HttpApi[F[_]: Async: Logger] private (
  services: Services[F],
  programs: Programs[F],
  appResources: AppResources[F],
  security: Security[F]
) extends Http4sDsl[F] {

  private val adminMiddleware =
    JwtAuthMiddleware[F, AdminUser](security.adminJwtAuth.value, security.adminAuth.findUser)

  private val usersMiddleware =
    JwtAuthMiddleware[F, CommonUser](security.userJwtAuth.value, security.usersAuth.findUser)

  implicit def userHttpErrorHandler: HttpErrorHandler[F, UserError] = new UserHttpErrorHandler[F]

  // Auth routes

  private val loginRoutes = LoginRoutes[F](security.auth).routes
//  private val logoutRoutes = LogoutRoutes[F](security.auth).routes(usersMiddleware)
  private val userRoutes  = UserRoutes[F](security.auth).routes

  // Admin routes

  private val adminTestRoutes = AdminTestRoutes[F]().routes(adminMiddleware)

  private val adminRoutes: HttpRoutes[F] =
    adminTestRoutes

  // Secured routes
  private val securedTestRoutes = SecuredTestRoutes[F]().routes(usersMiddleware)

  // Open routes
  private val testRoutes   = TestRoutes[F]().routes
  private val healthRoutes = HealthRoutes[F](services.healthCheck).routes
  //private val hookRoutes   = HookRoutes[F](services.hooks, appResources).routes

  // Combining all the http routes

  private val securedRoutes: HttpRoutes[F] = securedTestRoutes

  private val openRoutes: HttpRoutes[F] =
    healthRoutes <+> testRoutes <+> loginRoutes <+> userRoutes

  private val tapirRoutes: HttpRoutes[F] = TapirRoutes[F](services, programs).routes

  private val errorAction: HttpApp[F] => HttpApp[F] = { http: HttpApp[F] =>
    ErrorAction.httpApp[F](
      http,
      (req, thr) => Logger[F].info("Oops: " ++ thr.getMessage)
    )
  }

  private val loggers: HttpApp[F] => HttpApp[F] = { http: HttpApp[F] =>
    RequestLogger.httpApp(logHeaders = false, logBody = true)(http)
  }.andThen { http: HttpApp[F] =>
    ResponseLogger.httpApp(false, true)(http)
  }

  private val routes: HttpRoutes[F] = userHttpErrorHandler
    .handle(
      Router(
        version.v1              -> openRoutes,
        version.v1 + "/admin"   -> adminRoutes,
        version.v1 + "/secured" -> securedRoutes,
        version.v1              -> tapirRoutes
      )
    )

  val httpApp: HttpApp[F] = errorAction(routes.orNotFound) // loggers() обертку убрали

}
