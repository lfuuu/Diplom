package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.services.CallRawService
import _root_.com.mcn.diplom.domain.calls.CallRaw._

class CallRawEndpoints[F[_]: Sync](service: CallRawService[F]) {

  private val basePath = "call-raw"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[CallRaw], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[CallRaw]])
    .description("Get all raw call records")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Long, String, CallRaw, Any] = endpoint.get
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[CallRaw])
    .description("Get raw call record by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(CallRawId(id)).flatMap {
        case Some(raw) => raw.asRight[String].pure[F]
        case None      => s"Raw record $id not found".asLeft[CallRaw].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[CallRawCreateRequest, String, CallRawId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[CallRawCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[CallRawId]))
    .description("Create new raw call record")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid raw data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Long, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete raw call record by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(CallRawId(id))
        .flatMap(_ => ().asRight[String].pure[F])
        .handleErrorWith(ex => ex.getMessage.asLeft[Unit].pure[F])
    }

  val endpoints: List[ServerEndpoint[Any, F]] = List(
    getAllServerEndpoint,
    getByIdServerEndpoint,
    createServerEndpoint,
    deleteServerEndpoint
  )
}
