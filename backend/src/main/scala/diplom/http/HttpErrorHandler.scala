package com.mcn.diplom.http

import cats.MonadError
import cats.syntax.all._
import com.mcn.diplom.domain.InvalidClaimContent
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

// https://github.com/PeterPerhac/errorhandling-with-optics-http4s/blob/master/src/main/scala/uk/co/devproltd/errorhandling/UserServer.scala
// https://typelevel.org/blog/2018/08/25/http4s-error-handling-mtl.html
// https://typelevel.org/blog/2018/11/28/http4s-error-handling-mtl-2.html

trait HttpErrorHandler[F[_], E <: Throwable] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}

object HttpErrorHandler {
  def apply[F[_], E <: Throwable](implicit ev: HttpErrorHandler[F, E]) = ev
}

import cats.ApplicativeError
import cats.data.{ Kleisli, OptionT }
import com.mcn.diplom.domain.{ InvalidUserAge, UserAlreadyExists, UserError, UserNotFound }

object RoutesHttpErrorHandler {

  def apply[F[_], E <: Throwable](
    routes: HttpRoutes[F]
  )(handler: E => F[Response[F]])(implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { req: Request[F] =>
      OptionT {
        routes.run(req).value.handleErrorWith(e => handler(e).map(Option(_)))
      }
    }
}

class UserHttpErrorHandler[F[_]: Logger](implicit M: MonadError[F, UserError]) extends HttpErrorHandler[F, UserError] with Http4sDsl[F] {

  private val handler: UserError => F[Response[F]] = {
    case InvalidUserAge(age)         => BadRequest(s"Invalid age $age".asJson)
    case UserAlreadyExists(username) => Conflict(username.asJson)
    case InvalidClaimContent()       => Forbidden("Invalid access token")
    case UserNotFound(username)      =>
      Logger[F].error(s"UserNotFound ${username}") >> NotFound(s"UserNotFound: $username.asJson")
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler(routes)(handler)
}
