package com.mcn.diplom.http.routes.admin

import cats.effect.Sync
import cats.syntax.all._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

import _root_.com.mcn.diplom.services.AuthUsersService
import _root_.com.mcn.diplom.domain.auth.AuthUser._

class AuthUsersEndpoints[F[_]: Sync](service: AuthUsersService[F]) {

  private val basePath = "auth-users"

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[AuthUser], Any] = endpoint.get
    .in(basePath)
    .out(jsonBody[List[AuthUser]])
    .description("Get all users")

  val getAllServerEndpoint: ServerEndpoint[Any, F] =
    getAllEndpoint.serverLogicSuccess(_ => service.findAll)

  val getByIdEndpoint: PublicEndpoint[Long, String, AuthUser, Any] = endpoint.get
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(jsonBody[AuthUser])
    .description("Get user by ID")

  val getByIdServerEndpoint: ServerEndpoint[Any, F] =
    getByIdEndpoint.serverLogic { id =>
      service.findById(AuthUserId(id)).flatMap {
        case Some(u) => u.asRight[String].pure[F]
        case None    => s"User $id not found".asLeft[AuthUser].pure[F]
      }
    }

  val createEndpoint: PublicEndpoint[AuthUserCreateRequest, String, AuthUserId, Any] = endpoint.post
    .in(basePath)
    .in(jsonBody[AuthUserCreateRequest])
    .errorOut(statusCode(StatusCode.BadRequest).and(stringBody))
    .out(statusCode(StatusCode.Created).and(jsonBody[AuthUserId]))
    .description("Create new user")

  val createServerEndpoint: ServerEndpoint[Any, F] =
    createEndpoint.serverLogic { req =>
      service.create(req).map {
        case Some(id) => Right(id)
        case None     => Left("Invalid user data")
      }
    }

  val deleteEndpoint: PublicEndpoint[Long, String, Unit, Any] = endpoint.delete
    .in(basePath / path[Long]("id"))
    .errorOut(statusCode(StatusCode.NotFound).and(stringBody))
    .out(statusCode(StatusCode.NoContent))
    .description("Delete user by ID")

  val deleteServerEndpoint: ServerEndpoint[Any, F] =
    deleteEndpoint.serverLogic { id =>
      service
        .deleteById(AuthUserId(id))
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
