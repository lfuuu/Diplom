package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.services.AuthTrunksService
import _root_.com.mcn.diplom.domain.auth.AuthTrunk._

class AuthTrunksEndpoints[F[_]: Sync](service: AuthTrunksService[F]) {

  private val basePath = "auth-trunks"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[AuthTrunk], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[AuthTrunk]])
    .description("Get all auth trunks")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Int, String, AuthTrunk, Any] = endpoint.get
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[AuthTrunk])
    .description("Get trunk by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(AuthTrunkId(id)).flatMap {
        case Some(t) => t.asRight[String].pure[F]
        case None    => s"Trunk $id not found".asLeft[AuthTrunk].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[AuthTrunkCreateRequest, String, AuthTrunkId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[AuthTrunkCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[AuthTrunkId]))
    .description("Create new auth trunk")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid trunk data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Int, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Int]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete trunk by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(AuthTrunkId(id))
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
