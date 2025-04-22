package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import _root_.com.mcn.diplom.services.BillingServiceTrunksService
import _root_.com.mcn.diplom.domain.nispd.BillingServiceTrunk._

class BillingServiceTrunksEndpoints[F[_]: Sync](service: BillingServiceTrunksService[F]) {

  private val basePath = "billing-service-trunks"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[BillingServiceTrunk], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[BillingServiceTrunk]])
    .description("Get all service trunks")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Int, String, BillingServiceTrunk, Any] = endpoint.get
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[BillingServiceTrunk])
    .description("Get service trunk by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(BillingServiceTrunkId(id)).flatMap {
        case Some(t) => t.asRight[String].pure[F]
        case None    => s"Service trunk $id not found".asLeft[BillingServiceTrunk].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[BillingServiceTrunkCreateRequest, String, BillingServiceTrunkId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[BillingServiceTrunkCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[BillingServiceTrunkId]))
    .description("Create new service trunk")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid service trunk data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Int, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete service trunk by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(BillingServiceTrunkId(id))
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
