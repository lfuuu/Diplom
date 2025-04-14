package com.mcn.diplom.http.routes

import cats.Monad
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class TestRoutes[F[_]: Monad](
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/test"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
    case POST -> Root / "post" / name =>
      Ok(s"Hello-post, $name.")
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
