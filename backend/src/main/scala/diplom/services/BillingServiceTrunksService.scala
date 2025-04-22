package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.nispd.BillingServiceTrunk._

trait BillingServiceTrunksService[F[_]] {
  def create(trunk: BillingServiceTrunkCreateRequest): F[Option[BillingServiceTrunkId]]
  def findAll: F[List[BillingServiceTrunk]]
  def findById(id: BillingServiceTrunkId): F[Option[BillingServiceTrunk]]
  def deleteById(id: BillingServiceTrunkId): F[Unit]
}

object BillingServiceTrunksService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): BillingServiceTrunksService[F] =
    new BillingServiceTrunksService[F] {
      import BillingServiceTrunksSQL._

      override def findAll: F[List[BillingServiceTrunk]] =
        postgres.use(_.execute(selectAll))

      override def create(request: BillingServiceTrunkCreateRequest): F[Option[BillingServiceTrunkId]] =
        postgres.use { session =>
          session.prepare(insertServiceTrunk).flatMap(_.option(request))
        }

      override def findById(id: BillingServiceTrunkId): F[Option[BillingServiceTrunk]] =
        postgres.use { session =>
          session.prepare(findByIdServiceTrunk).flatMap(_.option(id))
        }

      override def deleteById(id: BillingServiceTrunkId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteByIdServiceTrunk)(id).void
        }
    }
}

private object BillingServiceTrunksSQL {

  val id: Codec[BillingServiceTrunkId] = int4.imap(BillingServiceTrunkId(_))(_.value)
  val clientId: Codec[BillingClientId] = int4.imap(BillingClientId(_))(_.value)
  val trunkId: Codec[BillingTrunkId]   = int4.imap(BillingTrunkId(_))(_.value)

  val activationDt: Codec[BillingServiceTrunkActivationDt] =
    timestamptz.imap(t => BillingServiceTrunkActivationDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val expireDt: Codec[Option[BillingServiceTrunkExpireDt]] =
    timestamptz.opt.imap(_.map(t => BillingServiceTrunkExpireDt(t.toInstant)))(_.map(_.value.atOffset(ZoneOffset.UTC)))
  val origEnabled: Codec[BillingServiceTrunkOrigEnabled]   = bool.imap(BillingServiceTrunkOrigEnabled(_))(_.value)
  val termEnabled: Codec[BillingServiceTrunkTermEnabled]   = bool.imap(BillingServiceTrunkTermEnabled(_))(_.value)

  val findAllCodec       = id *: clientId *: trunkId *: activationDt *: expireDt *: origEnabled *: termEnabled
  val createRequestCodec = clientId *: trunkId *: activationDt *: expireDt *: origEnabled *: termEnabled

  val selectAll: Query[Void, BillingServiceTrunk] =
    sql"""
      SELECT id, client_id, trunk_id, activation_dt, expire_dt, orig_enabled, term_enabled
      FROM billing.service_trunk
    """.query(findAllCodec).to[BillingServiceTrunk]

  val insertServiceTrunk: Query[BillingServiceTrunkCreateRequest, BillingServiceTrunkId] =
    sql"""
      INSERT INTO billing.service_trunk 
        (client_id, trunk_id, activation_dt, expire_dt, orig_enabled, term_enabled)
      VALUES ($clientId, $trunkId, $activationDt, $expireDt, $origEnabled, $termEnabled)
      RETURNING id
    """
      .query(int4)
      .contramap[BillingServiceTrunkCreateRequest] { req =>
        req.clientId *: req.trunkId *: req.activationDt *: req.expireDt *:
          req.origEnabled *: req.termEnabled *: EmptyTuple
      }
      .map(BillingServiceTrunkId(_))

  val findByIdServiceTrunk: Query[BillingServiceTrunkId, BillingServiceTrunk] =
    sql"""
      SELECT id, client_id, trunk_id, activation_dt, expire_dt, orig_enabled, term_enabled
      FROM billing.service_trunk
      WHERE id = $id
    """.query(findAllCodec).to[BillingServiceTrunk]

  val deleteByIdServiceTrunk: Command[BillingServiceTrunkId] =
    sql"""
      DELETE FROM billing.service_trunk
      WHERE id = $id
    """.command
}
