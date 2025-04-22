package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import _root_.com.mcn.diplom.domain.nispd.BillingPricelist._
import _root_.com.mcn.diplom.services.BillingPricelistsService

class BillingPricelistsEndpoints[F[_]: Sync](service: BillingPricelistsService[F]) {

  private val basePath = "billinig-pricelists"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[BillingPricelist], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[BillingPricelist]])
    .description("Get all pricelists")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Int, String, BillingPricelist, Any] = endpoint.get
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[BillingPricelist])
    .description("Get pricelist by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(BillingPricelistId(id)).flatMap {
        case Some(p) => p.asRight[String].pure[F]
        case None    => s"Pricelist $id not found".asLeft[BillingPricelist].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[BillingPricelistCreateRequest, String, BillingPricelistId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[BillingPricelistCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[BillingPricelistId]))
    .description("Create new pricelist")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid pricelist data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Int, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete pricelist by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(BillingPricelistId(id))
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
