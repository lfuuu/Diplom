package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.nispd.BillingServiceNumber._
import java.time.Instant

trait BillingServiceNumbersService[F[_]] {
  def create(number: BillingServiceNumberCreateRequest): F[Option[BillingServiceNumberId]]
  def findAll: F[List[BillingServiceNumber]]
  def findById(id: BillingServiceNumberId): F[Option[BillingServiceNumber]]
  def deleteById(id: BillingServiceNumberId): F[Unit]
  def findServiceNumberByNum(tm: Instant, num: BillingServiceNumberDID): F[Option[BillingServiceNumber]]
}

object BillingServiceNumbersService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): BillingServiceNumbersService[F] =
    new BillingServiceNumbersService[F] {
      import BillingServiceNumbersSQL._

      override def findAll: F[List[BillingServiceNumber]] =
        postgres.use(_.execute(selectAll))

      override def create(request: BillingServiceNumberCreateRequest): F[Option[BillingServiceNumberId]] =
        postgres.use { session =>
          session.prepare(insertServiceNumber).flatMap(_.option(request))
        }

      override def findById(id: BillingServiceNumberId): F[Option[BillingServiceNumber]] =
        postgres.use { session =>
          session.prepare(findByIdServiceNumber).flatMap(_.option(id))
        }

      override def deleteById(id: BillingServiceNumberId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteByIdServiceNumber)(id).void
        }

      override def findServiceNumberByNum(tm: Instant, num: BillingServiceNumberDID): F[Option[BillingServiceNumber]] =
        postgres.use { session =>
          session
            .prepare(findServiceNumberByNumServiceNumber)
            .flatMap(_.option((BillingServiceNumberActivationDt(tm), BillingServiceNumberExpireDt(tm), num)))
        }
    }
}

private object BillingServiceNumbersSQL {

  val id: Codec[BillingServiceNumberId]   = int4.imap(BillingServiceNumberId(_))(_.value)
  val clientId: Codec[BillingClientId]    = int4.imap(BillingClientId(_))(_.value)
  val did: Codec[BillingServiceNumberDID] = text.imap(BillingServiceNumberDID(_))(_.value)

  val activationDt: Codec[BillingServiceNumberActivationDt] =
    timestamptz.imap(t => BillingServiceNumberActivationDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val expireDt: Codec[Option[BillingServiceNumberExpireDt]] =
    timestamptz.opt.imap(_.map(t => BillingServiceNumberExpireDt(t.toInstant)))(_.map(_.value.atOffset(ZoneOffset.UTC)))

  val expireDtWo: Codec[BillingServiceNumberExpireDt] =
    timestamptz.imap(t => BillingServiceNumberExpireDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val findAllCodec       = id *: clientId *: did *: activationDt *: expireDt
  val createRequestCodec = clientId *: did *: activationDt *: expireDt

  val findServiceNumberByNumServiceNumber
    : Query[BillingServiceNumberActivationDt *: BillingServiceNumberExpireDt *: BillingServiceNumberDID *: EmptyTuple, BillingServiceNumber] =
    sql"""
      SELECT id, client_id, did, activation_dt, expire_dt
      FROM billing.service_number  where activation_dt <= $activationDt and  ( expire_dt > $expireDtWo or expire_dt is Null)
      and did = $did
      """.query(findAllCodec).to[BillingServiceNumber]

  val selectAll: Query[Void, BillingServiceNumber] =
    sql"""
      SELECT id, client_id, did, activation_dt, expire_dt
      FROM billing.service_number
    """.query(findAllCodec).to[BillingServiceNumber]

  val insertServiceNumber: Query[BillingServiceNumberCreateRequest, BillingServiceNumberId] =
    sql"""
      INSERT INTO billing.service_number (client_id, did, activation_dt, expire_dt)
      VALUES ($clientId, $did, $activationDt, $expireDt)
      RETURNING id
    """
      .query(int4)
      .contramap[BillingServiceNumberCreateRequest] { req =>
        req.clientId *: req.did *: req.activationDt *: req.expireDt *: EmptyTuple
      }
      .map(BillingServiceNumberId(_))

  val findByIdServiceNumber: Query[BillingServiceNumberId, BillingServiceNumber] =
    sql"""
      SELECT id, client_id, did, activation_dt, expire_dt
      FROM billing.service_number
      WHERE id = $id
    """.query(findAllCodec).to[BillingServiceNumber]

  val deleteByIdServiceNumber: Command[BillingServiceNumberId] =
    sql"""
      DELETE FROM billing.service_number
      WHERE id = $id
    """.command
}
