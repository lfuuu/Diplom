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

class CallCdrEndpoints[F[_]: Sync](service: CallCdrService[F]) {

  private val basePath = "call-cdr"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[CallCdr], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[CallCdr]])
    .description("Get all CDRs")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Long, String, CallCdr, Any] = endpoint.get
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[CallCdr])
    .description("Get CDR by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(CallCdrId(id)).flatMap {
        case Some(cdr) => cdr.asRight[String].pure[F]
        case None      => s"CDR $id not found".asLeft[CallCdr].pure[F]
      }
    }

  val endpoints: List[ServerEndpoint[Any, F]] = List(
    getAllServerEndpoint,
    getByIdServerEndpoint
  )
}
