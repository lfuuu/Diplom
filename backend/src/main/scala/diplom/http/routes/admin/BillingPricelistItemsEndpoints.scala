package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import _root_.com.mcn.diplom.services.BillingPricelistItemsService
import _root_.com.mcn.diplom.domain.nispd.BillingPricelistItem._

class BillingPricelistItemsEndpoints[F[_]: Sync](service: BillingPricelistItemsService[F]) {

  private val basePath = "billing-pricelist-items"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[BillingPricelistItem], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[BillingPricelistItem]])
    .description("Get all pricelist items")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Int, String, BillingPricelistItem, Any] = endpoint.get
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[BillingPricelistItem])
    .description("Get pricelist item by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(BillingPricelistItemId(id)).flatMap {
        case Some(item) => item.asRight[String].pure[F]
        case None       => s"Pricelist item $id not found".asLeft[BillingPricelistItem].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[BillingPricelistItemCreateRequest, String, BillingPricelistItemId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[BillingPricelistItemCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[BillingPricelistItemId]))
    .description("Create new pricelist item")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid pricelist item data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Int, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete pricelist item by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(BillingPricelistItemId(id))
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
