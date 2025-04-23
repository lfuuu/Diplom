package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.calls.CallsRaws._

trait CallsRawsService[F[_]] {
  def create(raw: CallsRawsCreateRequest): F[Option[CallsRawsId]]
  def findAll: F[List[CallsRaws]]
  def findById(id: CallsRawsId): F[Option[CallsRaws]]
  def deleteById(id: CallsRawsId): F[Unit]
}

object CallsRawsService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): CallsRawsService[F] =
    new CallsRawsService[F] {
      import CallsRawsSQL._

      override def findAll: F[List[CallsRaws]] =
        postgres.use(_.execute(selectAll))

      override def create(request: CallsRawsCreateRequest): F[Option[CallsRawsId]] =
        postgres.use { session =>
          session.prepare(insertRaw).flatMap(_.option(request))
        }

      override def findById(id: CallsRawsId): F[Option[CallsRaws]] =
        postgres.use { session =>
          session.prepare(findRawById).flatMap(_.option(id))
        }

      override def deleteById(id: CallsRawsId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteRawById)(id).void
        }
    }
}

private object CallsRawsSQL {

  val id: Codec[CallsRawsId]                           = int8.imap(CallsRawsId(_))(_.value)
  val orig: Codec[CallsRawsOrig]                       = bool.imap(CallsRawsOrig(_))(_.value)
  val peerId: Codec[CallsRawsPeerId]                   = int8.imap(CallsRawsPeerId(_))(_.value)
  val cdrId: Codec[CallsRawsCdrId]                     = int8.imap(CallsRawsCdrId(_))(_.value)

  val connectTime: Codec[CallsRawsConnectTime]         =
    timestamptz.imap(t => CallsRawsConnectTime(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))
  val trunkId: Codec[CallsRawsTrunkId]                 = int4.imap(CallsRawsTrunkId(_))(_.value)
  val clientId: Codec[CallsRawsClientId]               = int4.imap(CallsRawsClientId(_))(_.value)
  val serviceTrunkId: Codec[CallsRawsServiceTrunkId]   = int4.imap(CallsRawsServiceTrunkId(_))(_.value)
  val serviceNumberId: Codec[CallsRawsServiceNumberId] = int4.imap(CallsRawsServiceNumberId(_))(_.value)
  val srcNumber: Codec[CallsRawsSrcNumber]             = text.imap(CallsRawsSrcNumber(_))(_.value)
  val dstNumber: Codec[CallsRawsDstNumber]             = text.imap(CallsRawsDstNumber(_))(_.value)
  val billedTime: Codec[CallsRawsBilledTime]           = int4.imap(CallsRawsBilledTime(_))(_.value)
  val rate: Codec[CallsRawsRate]                       = numeric(12, 2).imap(CallsRawsRate(_))(_.value)
  val cost: Codec[CallsRawsCost]                       = numeric(12, 2).imap(CallsRawsCost(_))(_.value)
  val pricelistId: Codec[CallsRawsPricelistId]         = int4.imap(CallsRawsPricelistId(_))(_.value)
  val disconnectCause: Codec[CallsRawsDisconnectCause] = int2.imap(CallsRawsDisconnectCause(_))(_.value)

  val findAllCodec = id *: orig *: peerId *: cdrId *: connectTime *: trunkId *:
    clientId *: serviceTrunkId *: serviceNumberId *: srcNumber *: dstNumber *:
    billedTime *: rate *: cost *: pricelistId *: disconnectCause

  val selectAll: Query[Void, CallsRaws] =
    sql"""
      SELECT id, orig, peer_id, cdr_id, connect_time, trunk_id,
             client_id, service_trunk_id, service_number_id, src_number,
             dst_number, billed_time, rate, cost, pricelist_id, disconnect_cause
      FROM calls."raw"
    """.query(findAllCodec).to[CallsRaws]

  val insertRaw: Query[CallsRawsCreateRequest, CallsRawsId] =
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
      .contramap[CallsRawsCreateRequest] { req =>
        req.orig *: req.peerId *: req.cdrId *: req.connectTime *:
          req.trunkId *: req.clientId *: req.serviceTrunkId *:
          req.serviceNumberId *: req.srcNumber *: req.dstNumber *:
          req.billedTime *: req.rate *: req.cost *: req.pricelistId *:
          req.disconnectCause *: EmptyTuple
      }
      .map(CallsRawsId(_))

  val findRawById: Query[CallsRawsId, CallsRaws] =
    sql"""
      SELECT id, orig, peer_id, cdr_id, connect_time, trunk_id,
             client_id, service_trunk_id, service_number_id, src_number,
             dst_number, billed_time, rate, cost, pricelist_id, disconnect_cause
      FROM calls."raw"
      WHERE id = $id
    """.query(findAllCodec).to[CallsRaws]

  val deleteRawById: Command[CallsRawsId] =
    sql"""
      DELETE FROM calls."raw"
      WHERE id = $id
    """.command
}
