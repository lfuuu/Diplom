package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.services.CallsCdrsService
import _root_.com.mcn.diplom.domain.calls.CallsCdr._

class CallsCdrsEndpoints[F[_]: Sync](service: CallsCdrsService[F]) {

  private val basePath = "calls-cdrs"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[CallsCdr], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[CallsCdr]])
    .description("Get all CDRs")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Long, String, CallsCdr, Any] = endpoint.get
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[CallsCdr])
    .description("Get CDR by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(CallsCdrId(id)).flatMap {
        case Some(cdr) => cdr.asRight[String].pure[F]
        case None      => s"CDR $id not found".asLeft[CallsCdr].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[CallsCdrCreateRequest, String, CallsCdrId, Any] = endpoint.post
    .in("acc")
    .in(jsonBody[CallsCdrCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[CallsCdrId]))
    .description("Create new CDR")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid CDR data")
      }
    }

  val endpoints: List[ServerEndpoint[Any, F]] = List(
    getAllServerEndpoint,
    getByIdServerEndpoint,
    createServerEndpoint
  )
}
