package com.mcn.diplom.services

import java.time.ZoneOffset
import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.calls.CallCdr.CallCdrCreateRequest
import _root_.com.mcn.diplom.domain.calls.CallCdr._

trait CallCdrService[F[_]] {
  def create(cdr: CallCdrCreateRequest): F[Option[CallCdrId]]
  def findAll: F[List[CallCdr]]
  def findById(id: CallCdrId): F[Option[CallCdr]]
  def deleteById(id: CallCdrId): F[Unit]
}

object CallCdrService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): CallCdrService[F] =
    new CallCdrService[F] {
      import CallCdrSQL._

      override def findAll: F[List[CallCdr]] =
        postgres.use(_.execute(selectAll))

      override def create(request: CallCdrCreateRequest): F[Option[CallCdrId]] =
        postgres.use { session =>
          session.prepare(insertCdr).flatMap(_.option(request))
        }

      override def findById(id: CallCdrId): F[Option[CallCdr]] =
        postgres.use { session =>
          session.prepare(findCdrById).flatMap(_.option(id))
        }

      override def deleteById(id: CallCdrId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteCdrById)(id).void
        }
    }
}

private object CallCdrSQL {

  val id: Codec[CallCdrId]        = int8.imap(CallCdrId(_))(_.value)
  val callId: Codec[CallId]       = int8.imap(CallId(_))(_.value)
  val srcNumber: Codec[SrcNumber] = text.imap(SrcNumber(_))(_.value)
  val dstNumber: Codec[DstNumber] = text.imap(DstNumber(_))(_.value)

  val setupTime: Codec[Option[SetupTime]] =
    timestamptz.opt.imap(_.map(t => SetupTime(t.toInstant)))(_.map(_.value.atOffset(ZoneOffset.UTC)))

  val connectTime: Codec[ConnectTime] =
    timestamptz.imap(t => ConnectTime(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val disconnectTime: Codec[Option[DisconnectTime]] =
    timestamptz.opt.imap(_.map(t => DisconnectTime(t.toInstant)))(_.map(_.value.atOffset(ZoneOffset.UTC)))
  val sessionTime: Codec[SessionTime]               = int8.imap(SessionTime(_))(_.value)
  val disconnectCause: Codec[DisconnectCause]       = int2.imap(DisconnectCause(_))(_.value)
  val srcRoute: Codec[SrcRoute]                     = text.imap(SrcRoute(_))(_.value)
  val dstRoute: Codec[DstRoute]                     = text.imap(DstRoute(_))(_.value)

  val findAllCodec = id *: callId *: srcNumber *: dstNumber *: setupTime *: connectTime *:
    disconnectTime *: sessionTime *: disconnectCause *: srcRoute *: dstRoute

  val selectAll: Query[Void, CallCdr] =
    sql"""
      SELECT id, call_id, src_number, dst_number, setup_time, connect_time,
             disconnect_time, session_time, disconnect_cause, src_route, dst_route
      FROM calls.cdr
    """.query(findAllCodec).to[CallCdr]

  val insertCdr: Query[CallCdrCreateRequest, CallCdrId] =
    sql"""
      INSERT INTO calls.cdr (
        call_id, src_number, dst_number, setup_time, connect_time,
        disconnect_time, session_time, disconnect_cause, src_route, dst_route
      ) VALUES (
        $callId, $srcNumber, $dstNumber, $setupTime, $connectTime,
        $disconnectTime, $sessionTime, $disconnectCause, $srcRoute, $dstRoute
      )
      RETURNING id
    """
      .query(int8)
      .contramap[CallCdrCreateRequest] { req =>
        req.callId *: req.srcNumber *: req.dstNumber *: req.setupTime *:
          req.connectTime *: req.disconnectTime *: req.sessionTime *:
          req.disconnectCause *: req.srcRoute *: req.dstRoute *: EmptyTuple
      }
      .map(CallCdrId(_))

  val findCdrById: Query[CallCdrId, CallCdr] =
    sql"""
      SELECT id, call_id, src_number, dst_number, setup_time, connect_time,
             disconnect_time, session_time, disconnect_cause, src_route, dst_route
      FROM calls.cdr
      WHERE id = $id
    """.query(findAllCodec).to[CallCdr]

  val deleteCdrById: Command[CallCdrId] =
    sql"""
      DELETE FROM calls.cdr
      WHERE id = $id
    """.command
}
