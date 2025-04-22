package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.domain.nispd.BillingPacket._
import _root_.com.mcn.diplom.services.BillingPacketsService

class BillingBillingPacketsEndpoints[F[_]: Sync](service: BillingPacketsService[F]) {

  private val basePath = "BillingPackets"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[BillingPacket], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[BillingPacket]])
    .description("Get all BillingPackets")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Long, String, BillingPacket, Any] = endpoint.get
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[BillingPacket])
    .description("Get BillingPacket by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(BillingPacketId(id)).flatMap {
        case Some(p) => p.asRight[String].pure[F]
        case None    => s"BillingPacket $id not found".asLeft[BillingPacket].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[BillingPacketCreateRequest, String, BillingPacketId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[BillingPacketCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[BillingPacketId]))
    .description("Create a new BillingPacket")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid BillingPacket data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Long, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete BillingPacket by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(BillingPacketId(id))
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
