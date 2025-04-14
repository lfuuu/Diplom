package com.mcn.diplom.http.routes

import cats.Monad
import com.mcn.diplom.http.auth.users.AdminUser
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{ AuthMiddleware, Router }

final case class AdminTestRoutes[F[_]: Monad](
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/test"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      case ar @ POST -> Root / "hello" as _ =>
        Ok(s"Hello! - ${ar.req}")
    }

  // private val httpRoutes: AuthedRoutes[AdminUser, F] =
  //   AuthedRoutes.of {
  //     case ar @ POST -> Root as _ =>
  //       ar.req.decodeR[BrandParam] { bp =>
  //         brands.create(bp.toDomain).flatMap { id =>
  //           Created(JsonObject.singleton("brand_id", id.asJson))
  //         }
  //       }
  //   }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] =
    Router(
      prefixPath -> authMiddleware(httpRoutes)
    )

}
