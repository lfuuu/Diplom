package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.nispd.BillingPacket._
import java.time.Instant

trait BillingPacketsService[F[_]] {
  def create(packet: BillingPacketCreateRequest): F[Option[BillingPacketId]]
  def findAll: F[List[BillingPacket]]
  def findById(id: BillingPacketId): F[Option[BillingPacket]]
  def deleteById(id: BillingPacketId): F[Unit]

  def getPricelistsForServiceNumberId(
    serviceNumberId: BillingPacketServiceNumberId,
    tm: Instant,
    orig: BillingPacketOrig
  ): F[List[BillingPacketPricelistId]]

  def getPricelistsForServiceTrunkId(
    serviceTrunkId: BillingPacketServiceTrunkId,
    tm: Instant,
    orig: BillingPacketOrig
  ): F[List[BillingPacketPricelistId]]
}

object BillingPacketsService {

  def make[F[_]: MonadCancelThrow: Concurrent](
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

      override def getPricelistsForServiceNumberId(
        serviceNumberId: BillingPacketServiceNumberId,
        tm: Instant,
        orig: BillingPacketOrig
      ): F[List[BillingPacketPricelistId]] =
        postgres.use(_.execute(findPricelistsForServiceNumberId)((BillingPacketActivationDt(tm), BillingPacketExpireDt(tm), serviceNumberId, orig)))

      override def getPricelistsForServiceTrunkId(
        serviceTrunkId: BillingPacketServiceTrunkId,
        tm: Instant,
        orig: BillingPacketOrig
      ): F[List[BillingPacketPricelistId]] =
        postgres.use(_.execute(findPricelistsForServiceTrunkId)((BillingPacketActivationDt(tm), BillingPacketExpireDt(tm), serviceTrunkId, orig)))

    }
}

private object BillingPacketsSQL {

  val id: Codec[BillingPacketId]                           = int8.imap(BillingPacketId(_))(_.value)
  val serviceTrunkId: Codec[BillingPacketServiceTrunkId]   = int4.imap(BillingPacketServiceTrunkId(_))(_.value)
  val serviceNumberId: Codec[BillingPacketServiceNumberId] = int4.imap(BillingPacketServiceNumberId(_))(_.value)
  val activationDt: Codec[BillingPacketActivationDt]       = timestamptz.imap(t => BillingPacketActivationDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val expireDt: Codec[Option[BillingPacketExpireDt]] =
    timestamptz.opt.imap(_.map(t => BillingPacketExpireDt(t.toInstant)))(_.map(_.value.atOffset(ZoneOffset.UTC)))

  val expireDtWo: Codec[BillingPacketExpireDt] =
    timestamptz.imap(t => BillingPacketExpireDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val orig: Codec[BillingPacketOrig]               = bool.imap(BillingPacketOrig(_))(_.value)
  val pricelistId: Codec[BillingPacketPricelistId] = int4.imap(BillingPacketPricelistId(_))(_.value)

  val findAllCodec       = id *: serviceTrunkId *: serviceNumberId *: activationDt *: expireDt *: orig *: pricelistId
  val createRequestCodec = serviceTrunkId *: serviceNumberId *: activationDt *: expireDt *: orig *: pricelistId

  val findPricelistsForServiceNumberId: Query[
    BillingPacketActivationDt *: BillingPacketExpireDt *: BillingPacketServiceNumberId *: BillingPacketOrig *: EmptyTuple,
    BillingPacketPricelistId
  ] =
    sql"""
      SELECT pricelist_id
      FROM billing.packet
      where activation_dt <= $activationDt and  ( expire_dt > $expireDtWo or expire_dt is Null)
      and service_number_id = $serviceNumberId and orig = $orig
      """.query(pricelistId)

  val findPricelistsForServiceTrunkId: Query[
    BillingPacketActivationDt *: BillingPacketExpireDt *: BillingPacketServiceTrunkId *: BillingPacketOrig *: EmptyTuple,
    BillingPacketPricelistId
  ] =
    sql"""
      SELECT pricelist_id
      FROM billing.packet
      where activation_dt <= $activationDt and  ( expire_dt > $expireDtWo or expire_dt is Null)
      and service_trunk_id = $serviceTrunkId and orig = $orig
      """.query(pricelistId)

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
