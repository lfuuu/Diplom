package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.calls.CallRaw._

trait CallRawService[F[_]] {
  def create(raw: CallRawCreateRequest): F[Option[CallRawId]]
  def findAll: F[List[CallRaw]]
  def findById(id: CallRawId): F[Option[CallRaw]]
  def deleteById(id: CallRawId): F[Unit]
}

object CallRawService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): CallRawService[F] =
    new CallRawService[F] {
      import CallRawSQL._

      override def findAll: F[List[CallRaw]] =
        postgres.use(_.execute(selectAll))

      override def create(request: CallRawCreateRequest): F[Option[CallRawId]] =
        postgres.use { session =>
          session.prepare(insertRaw).flatMap(_.option(request))
        }

      override def findById(id: CallRawId): F[Option[CallRaw]] =
        postgres.use { session =>
          session.prepare(findRawById).flatMap(_.option(id))
        }

      override def deleteById(id: CallRawId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteRawById)(id).void
        }
    }
}

private object CallRawSQL {

  val id: Codec[CallRawId]         = int8.imap(CallRawId(_))(_.value)
  val orig: Codec[CallRawOrig]     = bool.imap(CallRawOrig(_))(_.value)
  val peerId: Codec[CallRawPeerId] = int8.imap(CallRawPeerId(_))(_.value)
  val cdrId: Codec[CallRawCdrId]   = int8.imap(CallRawCdrId(_))(_.value)

  val connectTime: Codec[CallRawConnectTime]         =
    timestamptz.imap(t => CallRawConnectTime(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))
  val trunkId: Codec[CallRawTrunkId]                 = int4.imap(CallRawTrunkId(_))(_.value)
  val clientId: Codec[CallRawClientId]               = int4.imap(CallRawClientId(_))(_.value)
  val serviceTrunkId: Codec[CallRawServiceTrunkId]   = int4.imap(CallRawServiceTrunkId(_))(_.value)
  val serviceNumberId: Codec[CallRawServiceNumberId] = int4.imap(CallRawServiceNumberId(_))(_.value)
  val srcNumber: Codec[CallRawSrcNumber]             = text.imap(CallRawSrcNumber(_))(_.value)
  val dstNumber: Codec[CallRawDstNumber]             = text.imap(CallRawDstNumber(_))(_.value)
  val billedTime: Codec[CallRawBilledTime]           = int4.imap(CallRawBilledTime(_))(_.value)
  val rate: Codec[CallRawRate]                       = numeric(12, 2).imap(CallRawRate(_))(_.value)
  val cost: Codec[CallRawCost]                       = numeric(12, 2).imap(CallRawCost(_))(_.value)
  val pricelistId: Codec[CallRawPricelistId]         = int4.imap(CallRawPricelistId(_))(_.value)
  val disconnectCause: Codec[CallRawDisconnectCause] = int2.imap(CallRawDisconnectCause(_))(_.value)

  val findAllCodec = id *: orig *: peerId *: cdrId *: connectTime *: trunkId *:
    clientId *: serviceTrunkId *: serviceNumberId *: srcNumber *: dstNumber *:
    billedTime *: rate *: cost *: pricelistId *: disconnectCause

  val selectAll: Query[Void, CallRaw] =
    sql"""
      SELECT id, orig, peer_id, cdr_id, connect_time, trunk_id,
             client_id, service_trunk_id, service_number_id, src_number,
             dst_number, billed_time, rate, cost, pricelist_id, disconnect_cause
      FROM calls."raw"
    """.query(findAllCodec).to[CallRaw]

  val insertRaw: Query[CallRawCreateRequest, CallRawId] =
    sql"""
      INSERT INTO calls."raw" (
        orig, peer_id, cdr_id, connect_time, trunk_id, client_id,
        service_trunk_id, service_number_id, src_number, dst_number,
        billed_time, rate, cost, pricelist_id, disconnect_cause
      ) VALUES (
        $orig, $peerId, $cdrId, $connectTime, $trunkId, $clientId,
        $serviceTrunkId, $serviceNumberId, $srcNumber, $dstNumber,
        $billedTime, $rate, $cost, $pricelistId, $disconnectCause
      )
      RETURNING id
    """
      .query(int8)
      .contramap[CallRawCreateRequest] { req =>
        req.orig *: req.peerId *: req.cdrId *: req.connectTime *:
          req.trunkId *: req.clientId *: req.serviceTrunkId *:
          req.serviceNumberId *: req.srcNumber *: req.dstNumber *:
          req.billedTime *: req.rate *: req.cost *: req.pricelistId *:
          req.disconnectCause *: EmptyTuple
      }
      .map(CallRawId(_))

  val findRawById: Query[CallRawId, CallRaw] =
    sql"""
      SELECT id, orig, peer_id, cdr_id, connect_time, trunk_id,
             client_id, service_trunk_id, service_number_id, src_number,
             dst_number, billed_time, rate, cost, pricelist_id, disconnect_cause
      FROM calls."raw"
      WHERE id = $id
    """.query(findAllCodec).to[CallRaw]

  val deleteRawById: Command[CallRawId] =
    sql"""
      DELETE FROM calls."raw"
      WHERE id = $id
    """.command
}
