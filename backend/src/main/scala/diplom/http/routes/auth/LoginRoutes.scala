package com.mcn.diplom.http.routes

import cats.MonadThrow
import cats.syntax.all._
import com.mcn.diplom.domain.Auth._
import com.mcn.diplom.ext.http4s.refined._
import com.mcn.diplom.services.Auth
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

final case class LoginRoutes[F[_]: JsonDecoder: MonadThrow: Logger](
  auth: Auth[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/auth"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ POST -> Root / "login" =>
      req.decodeR[LoginUser] { user =>
        auth
          .login(user.username.toDomain, user.password.toDomain)
          .attemptTap {
            case a @ Left(_) => Logger[F].warn(s"Ошибка ${a.toString}")
            case _           => MonadThrow[F].unit
          }
          .flatMap(Ok(_))
          .recoverWith {
            case UserNotFound(_) | InvalidPassword(_) => Forbidden()
          }
      }

  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
