package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.services.CallsRawsService
import _root_.com.mcn.diplom.domain.calls.CallsRaws._

class CallsRawsEndpoints[F[_]: Sync](service: CallsRawsService[F]) {

  private val basePath = "calls-raws"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[CallsRaws], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[CallsRaws]])
    .description("Get all raw call records")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Long, String, CallsRaws, Any] = endpoint.get
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[CallsRaws])
    .description("Get raw call record by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(CallsRawsId(id)).flatMap {
        case Some(raw) => raw.asRight[String].pure[F]
        case None      => s"Raw record $id not found".asLeft[CallsRaws].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[CallsRawsCreateRequest, String, CallsRawsId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[CallsRawsCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[CallsRawsId]))
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
        .deleteById(CallsRawsId(id))
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
