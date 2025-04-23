package com.mcn.diplom.services

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.auth.AuthUser._

trait AuthUsersService[F[_]] {
  def create(user: AuthUserCreateRequest): F[Option[AuthUserId]]
  def findAll: F[List[AuthUser]]
  def findById(id: AuthUserId): F[Option[AuthUser]]
  def deleteById(id: AuthUserId): F[Unit]
}

object AuthUsersService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): AuthUsersService[F] =
    new AuthUsersService[F] {
      import AuthUsersSQL._

      override def findAll: F[List[AuthUser]] =
        postgres.use(_.execute(selectAll))

      override def create(request: AuthUserCreateRequest): F[Option[AuthUserId]] =
        postgres.use { session =>
          session.prepare(insertUser).flatMap(_.option(request))
        }

      override def findById(id: AuthUserId): F[Option[AuthUser]] =
        postgres.use { session =>
          session.prepare(findUserById).flatMap(_.option(id))
        }

      override def deleteById(id: AuthUserId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteUserById)(id).void
        }
    }
}

private object AuthUsersSQL {

  val id: Codec[AuthUserId] = int8.imap(AuthUserId(_))(_.value)

  val clientId: Codec[AuthClientId] =
    int4.imap(AuthClientId(_))(_.value)

  val login: Codec[AuthUserLogin] =
    text.imap(AuthUserLogin(_))(_.value)

  val password: Codec[AuthUserPassword] =
    text.imap(AuthUserPassword(_))(_.value)

  val active: Codec[AuthUserActive] =
    bool.imap(AuthUserActive(_))(_.value)

  val findAllCodec       = id *: clientId *: login *: password *: active
  val createRequestCodec = clientId *: login *: password *: active

  val selectAll: Query[Void, AuthUser] =
    sql"""
      SELECT id, client_id, login, password, active
      FROM auth."user"
    """.query(findAllCodec).to[AuthUser]

  val insertUser: Query[AuthUserCreateRequest, AuthUserId] =
    sql"""
      INSERT INTO auth."user" (client_id, login, password, active)
      VALUES ($clientId, $login, $password, $active)
      RETURNING id
    """
      .query(int8)
      .contramap[AuthUserCreateRequest] { req =>
        req.clientId *: req.login *: req.password *: req.active *: EmptyTuple
      }
      .map(AuthUserId(_))

  val findUserById: Query[AuthUserId, AuthUser] =
    sql"""
      SELECT id, client_id, login, password, active
      FROM auth."user"
      WHERE id = $id
    """.query(findAllCodec).to[AuthUser]

  val deleteUserById: Command[AuthUserId] =
    sql"""
      DELETE FROM auth."user"
      WHERE id = $id
    """.command
}
