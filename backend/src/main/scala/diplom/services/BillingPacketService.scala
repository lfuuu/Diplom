package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.nispd.BillingPacket._

trait BillingPacketsService[F[_]] {
  def create(packet: BillingPacketCreateRequest): F[Option[BillingPacketId]]
  def findAll: F[List[BillingPacket]]
  def findById(id: BillingPacketId): F[Option[BillingPacket]]
  def deleteById(id: BillingPacketId): F[Unit]
}

object BillingPacketsService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): BillingPacketsService[F] =
    new BillingPacketsService[F] {
      import BillingPacketsSQL._

      override def findAll: F[List[BillingPacket]] =
        postgres.use(_.execute(selectAll))

      override def create(request: BillingPacketCreateRequest): F[Option[BillingPacketId]] =
        postgres.use { session =>
          session.prepare(insertPacket).flatMap(_.option(request))
        }

      override def findById(id: BillingPacketId): F[Option[BillingPacket]] =
        postgres.use { session =>
          session.prepare(findByIdPacket).flatMap(_.option(id))
        }

      override def deleteById(id: BillingPacketId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteByIdPacket)(id).void
        }
    }
}

private object BillingPacketsSQL {

  val id: Codec[BillingPacketId]                     = int8.imap(BillingPacketId(_))(_.value)
  val serviceTrunkId: Codec[BillingServiceTrunkId]   = int4.imap(BillingServiceTrunkId(_))(_.value)
  val serviceNumberId: Codec[BillingServiceNumberId] = int4.imap(BillingServiceNumberId(_))(_.value)
  val activationDt: Codec[BillingPacketActivationDt] = timestamptz.imap(t => BillingPacketActivationDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val expireDt: Codec[Option[BillingPacketExpireDt]] =
    timestamptz.opt.imap(_.map(t => BillingPacketExpireDt(t.toInstant)))(_.map(_.value.atOffset(ZoneOffset.UTC)))
  val orig: Codec[BillingPacketOrig]                 = bool.imap(BillingPacketOrig(_))(_.value)
  val pricelistId: Codec[BillingPricelistId]         = int4.imap(BillingPricelistId(_))(_.value)

  val findAllCodec       = id *: serviceTrunkId *: serviceNumberId *: activationDt *: expireDt *: orig *: pricelistId
  val createRequestCodec = serviceTrunkId *: serviceNumberId *: activationDt *: expireDt *: orig *: pricelistId

  val selectAll: Query[Void, BillingPacket] =
    sql"""
      SELECT id, service_trunk_id, service_number_id, activation_dt, expire_dt, orig, pricelist_id
      FROM billing.packet
    """.query(findAllCodec).to[BillingPacket]

  val insertPacket: Query[BillingPacketCreateRequest, BillingPacketId] =
    sql"""
      INSERT INTO billing.packet (service_trunk_id, service_number_id, activation_dt, expire_dt, orig, pricelist_id)
      VALUES ($serviceTrunkId, $serviceNumberId, $activationDt, $expireDt, $orig, $pricelistId)
      RETURNING id
    """
      .query(int8)
      .contramap[BillingPacketCreateRequest] { req =>
        req.serviceTrunkId *: req.serviceNumberId *: req.activationDt *: req.expireDt *: req.orig *: req.pricelistId *: EmptyTuple
      }
      .map(BillingPacketId(_))

  val findByIdPacket: Query[BillingPacketId, BillingPacket] =
    sql"""
      SELECT id, service_trunk_id, service_number_id, activation_dt, expire_dt, orig, pricelist_id
      FROM billing.packet
      WHERE id = $id
    """.query(findAllCodec).to[BillingPacket]

  val deleteByIdPacket: Command[BillingPacketId] =
    sql"""
      DELETE FROM billing.packet
      WHERE id = $id
    """.command
}
