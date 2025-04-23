package com.mcn.diplom.services

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.auth.AuthTrunk._

trait AuthTrunksService[F[_]] {
  def create(trunk: AuthTrunkCreateRequest): F[Option[AuthTrunkId]]
  def findAll: F[List[AuthTrunk]]
  def findById(id: AuthTrunkId): F[Option[AuthTrunk]]
  def deleteById(id: AuthTrunkId): F[Unit]
}

object AuthTrunksService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): AuthTrunksService[F] =
    new AuthTrunksService[F] {
      import AuthTrunksSQL._

      override def findAll: F[List[AuthTrunk]] =
        postgres.use(_.execute(selectAll))

      override def create(request: AuthTrunkCreateRequest): F[Option[AuthTrunkId]] =
        postgres.use { session =>
          session.prepare(insertTrunk).flatMap(_.option(request))
        }

      override def findById(id: AuthTrunkId): F[Option[AuthTrunk]] =
        postgres.use { session =>
          session.prepare(findTrunkById).flatMap(_.option(id))
        }

      override def deleteById(id: AuthTrunkId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteTrunkById)(id).void
        }
    }
}

private object AuthTrunksSQL {

  val id: Codec[AuthTrunkId] = int4.imap(AuthTrunkId(_))(_.value)

  val trunkName: Codec[Option[AuthTrunkName]] =
    text.opt.imap(_.map(AuthTrunkName(_)))(_.map(_.value))

  val authByNumber: Codec[AuthTrunkAuthByNumber] =
    bool.imap(AuthTrunkAuthByNumber(_))(_.value)

  val findAllCodec       = id *: trunkName *: authByNumber
  val createRequestCodec = trunkName *: authByNumber

  val selectAll: Query[Void, AuthTrunk] =
    sql"""
      SELECT id, trunk_name, auth_by_number 
      FROM auth.trunk
    """.query(findAllCodec).to[AuthTrunk]

  val insertTrunk: Query[AuthTrunkCreateRequest, AuthTrunkId] =
    sql"""
      INSERT INTO auth.trunk (trunk_name, auth_by_number)
      VALUES ($trunkName, $authByNumber)
      RETURNING id
    """
      .query(int4)
      .contramap[AuthTrunkCreateRequest] { req =>
        req.trunkName *: req.authByNumber *: EmptyTuple
      }
      .map(AuthTrunkId(_))

  val findTrunkById: Query[AuthTrunkId, AuthTrunk] =
    sql"""
      SELECT id, trunk_name, auth_by_number
      FROM auth.trunk
      WHERE id = $id
    """.query(findAllCodec).to[AuthTrunk]

  val deleteTrunkById: Command[AuthTrunkId] =
    sql"""
      DELETE FROM auth.trunk 
      WHERE id = $id
    """.command
}
