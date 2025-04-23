package com.mcn.diplom.domain.calls

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.Instant
import sttp.tapir.derevo.schema

object CallsCdr {

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsCdrId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class SrcNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class DstNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class SetupTime(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class ConnectTime(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class DisconnectTime(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class SessionTime(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class DisconnectCause(value: Short)

  @derive(decoder, encoder, schema)
  @newtype
  case class SrcRoute(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class DstRoute(value: String)

  @derive(decoder, encoder, schema)
  case class CallsCdrCreateRequest(
    callId: CallId,
    srcNumber: SrcNumber,
    dstNumber: DstNumber,
    setupTime: Option[SetupTime],
    connectTime: ConnectTime,
    disconnectTime: Option[DisconnectTime],
    sessionTime: SessionTime,
    disconnectCause: DisconnectCause,
    srcRoute: SrcRoute,
    dstRoute: DstRoute
  )

  @derive(decoder, encoder, schema)
  case class CallsCdr(
    id: CallsCdrId,
    callId: CallId,
    srcNumber: SrcNumber,
    dstNumber: DstNumber,
    setupTime: Option[SetupTime],
    connectTime: ConnectTime,
    disconnectTime: Option[DisconnectTime],
    sessionTime: SessionTime,
    disconnectCause: DisconnectCause,
    srcRoute: SrcRoute,
    dstRoute: DstRoute
  )
}
