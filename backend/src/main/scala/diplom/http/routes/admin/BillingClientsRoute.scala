package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import _root_.com.mcn.diplom.services.BillingClientsService
import _root_.com.mcn.diplom.domain.nispd.BillingClient._

class BillingClientsEndpoints[F[_]: Sync](billingClientsService: BillingClientsService[F]) {

  private val basePath = "billing-clients"

  // Get all billing clients
  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[BillingClient], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[BillingClient]])
    .description("Retrieve all billing clients")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => billingClientsService.findAll)

  // Get billing client by ID
  //val getByIdEndpoint: PublicEndpoint[Int, (StatusCode, String), BillingClient, Any] = endpoint.get
  val getByIdEndpoint: PublicEndpoint[Int, String, BillingClient, Any] = endpoint.get
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[BillingClient])
    .description("Retrieve a billing client by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      billingClientsService.findById(BillingClientId(id)).flatMap {
        case Some(client) => client.asRight[String].pure[F]
        case None         => s"Client with id $id not found".asLeft[BillingClient].pure[F]
      }
    }

  // // Create new billing client
  val createEndpoint: PublicEndpoint[BillingClientCreateRequest, String, BillingClientId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[BillingClientCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[BillingClientId]))
    .description("Create a new billing client")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { request =>
      billingClientsService.create(request).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid client data")
      }
    }

  // // Delete billing client by ID
  val deleteEndpoint: PublicEndpoint[Int, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete a billing client by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      billingClientsService
        .deleteById(BillingClientId(id))
        .flatMap(_ => ().asRight[String].pure[F])
        .handleErrorWith { ex =>
          ex.getMessage.asLeft[Unit].pure[F]
        }
    }

  val endpoints: List[ServerEndpoint[Any, F]] =
    List(getAllServerEndpoint, getByIdServerEndpoint, createServerEndpoint, deleteServerEndpoint)
}
