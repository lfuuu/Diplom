package com.mcn.diplom.domain.calls

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.Instant
import sttp.tapir.derevo.schema

object CallRaw {

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawOrig(value: Boolean)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawPeerId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawCdrId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawConnectTime(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawClientId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawServiceTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawServiceNumberId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawSrcNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawDstNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawBilledTime(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawRate(value: BigDecimal)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawCost(value: BigDecimal)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawPricelistId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallRawDisconnectCause(value: Short)

  @derive(decoder, encoder, schema)
  case class CallRawCreateRequest(
    orig: CallRawOrig,
    peerId: CallRawPeerId,
    cdrId: CallRawCdrId,
    connectTime: CallRawConnectTime,
    trunkId: CallRawTrunkId,
    clientId: CallRawClientId,
    serviceTrunkId: Option[CallRawServiceTrunkId],
    serviceNumberId: Option[CallRawServiceNumberId],
    srcNumber: CallRawSrcNumber,
    dstNumber: CallRawDstNumber,
    billedTime: CallRawBilledTime,
    rate: CallRawRate,
    cost: CallRawCost,
    pricelistId: CallRawPricelistId,
    disconnectCause: CallRawDisconnectCause
  )

  @derive(decoder, encoder, schema)
  case class CallRaw(
    id: CallRawId,
    orig: CallRawOrig,
    peerId: CallRawPeerId,
    cdrId: CallRawCdrId,
    connectTime: CallRawConnectTime,
    trunkId: CallRawTrunkId,
    clientId: CallRawClientId,
    serviceTrunkId: Option[CallRawServiceTrunkId],
    serviceNumberId: Option[CallRawServiceNumberId],
    srcNumber: CallRawSrcNumber,
    dstNumber: CallRawDstNumber,
    billedTime: CallRawBilledTime,
    rate: CallRawRate,
    cost: CallRawCost,
    pricelistId: CallRawPricelistId,
    disconnectCause: CallRawDisconnectCause
  )
}
