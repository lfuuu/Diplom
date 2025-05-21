package com.mcn.diplom.http.routes.admin

import _root_.com.mcn.diplom.domain.nispd.AuthRequest._
import _root_.com.mcn.diplom.domain.nispd.AuthRequestError._
import _root_.com.mcn.diplom.domain.nispd.AuthResponse._
import _root_.com.mcn.diplom.programs.AuthAndRouteCall
import cats.effect.Sync
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

class AuthEndpoints[F[_]: Sync](authAndRouteCall: AuthAndRouteCall[F]) {

  val authEndpoint: PublicEndpoint[AuthRequest, AuthRequestError, AuthResponse, Any] = endpoint.post
    .in("doAuth")
    .in(jsonBody[AuthRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[AuthRequestError]))
    .out(statusCode(StatusCode.Created).and(jsonBody[AuthResponse]))
    .description("doAuth")

  val authServerEndpoint: ServerEndpoint[Any, F] =
    authEndpoint.serverLogic { req =>
      authAndRouteCall.doAuthAndRoute(req).value
    }

  val endpoints: List[ServerEndpoint[Any, F]] = List(
    authServerEndpoint
  )
}
