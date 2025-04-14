package com.mcn.diplom.http.routes.secured

import cats.Monad
import com.mcn.diplom.http.auth.users.CommonUser
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{ AuthMiddleware, Router }

final case class SecuredTestRoutes[F[_]: Monad](
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/test"

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of {

    case _ @POST -> Root / "hello" as user =>
      Ok(s"Hello, ${user.value.name}")

    // case ar @ POST -> Root  / "hello" / name as user =>
    //   ar.req.decodeR[Card] { card =>
    //     checkout
    //       .process(user.value.id, card)
    //       .flatMap(Created(_))
    //   }

  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] =
    Router(
      prefixPath -> authMiddleware(httpRoutes)
    )

}
