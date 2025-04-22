package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import _root_.com.mcn.diplom.services.BillingServiceNumbersService
import _root_.com.mcn.diplom.domain.nispd.BillingServiceNumber._

class BillingServiceNumbersEndpoints[F[_]: Sync](service: BillingServiceNumbersService[F]) {

  private val basePath = "billing-service-numbers"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[BillingServiceNumber], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[BillingServiceNumber]])
    .description("Get all service numbers")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Int, String, BillingServiceNumber, Any] = endpoint.get
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[BillingServiceNumber])
    .description("Get service number by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(BillingServiceNumberId(id)).flatMap {
        case Some(n) => n.asRight[String].pure[F]
        case None    => s"Service number $id not found".asLeft[BillingServiceNumber].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[BillingServiceNumberCreateRequest, String, BillingServiceNumberId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[BillingServiceNumberCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[BillingServiceNumberId]))
    .description("Create a new service number")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid service number data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Int, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete service number by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(BillingServiceNumberId(id))
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
