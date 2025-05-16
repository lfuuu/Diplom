package com.mcn.diplom.http.routes.admin

package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.services.CallCdrService
import _root_.com.mcn.diplom.domain.calls.CallCdr._
import _root_.com.mcn.diplom.programs.BillingCall

class AccEndpoints[F[_]: Sync](callCdrService: CallCdrService[F], billingCall: BillingCall[F]) {

  val accEndpoint: PublicEndpoint[CallCdrCreateRequest, String, CallCdrId, Any] = endpoint.post
    .in("acc")
    .in(jsonBody[CallCdrCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[CallCdrId]))
    .description("Create new CDR")

  val accServerEndpoint: ServerEndpoint[Any, F] =
    accEndpoint.serverLogic { req =>
      callCdrService.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid CDR data")
      }
    }

  val testCallEndpoint: PublicEndpoint[CallCdrCreateRequest, String, CallCdrId, Any] = endpoint.post
    .in("testCall")
    .in(jsonBody[CallCdrCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[CallCdrId]))
    .description("Create new CDR")

  val endpoints: List[ServerEndpoint[Any, F]] = List(
    accServerEndpoint
  )
}
