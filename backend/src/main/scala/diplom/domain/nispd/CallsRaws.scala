package com.mcn.diplom.domain.calls

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.Instant
import sttp.tapir.derevo.schema

object CallsRaws {

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsOrig(value: Boolean)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsPeerId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsCdrId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsConnectTime(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsClientId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsServiceTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsServiceNumberId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsSrcNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsDstNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsBilledTime(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsRate(value: BigDecimal)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsCost(value: BigDecimal)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsPricelistId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class CallsRawsDisconnectCause(value: Short)

  @derive(decoder, encoder, schema)
  case class CallsRawsCreateRequest(
    orig: CallsRawsOrig,
    peerId: CallsRawsPeerId,
    cdrId: CallsRawsCdrId,
    connectTime: CallsRawsConnectTime,
    trunkId: CallsRawsTrunkId,
    clientId: CallsRawsClientId,
    serviceTrunkId: CallsRawsServiceTrunkId,
    serviceNumberId: CallsRawsServiceNumberId,
    srcNumber: CallsRawsSrcNumber,
    dstNumber: CallsRawsDstNumber,
    billedTime: CallsRawsBilledTime,
    rate: CallsRawsRate,
    cost: CallsRawsCost,
    pricelistId: CallsRawsPricelistId,
    disconnectCause: CallsRawsDisconnectCause
  )

  @derive(decoder, encoder, schema)
  case class CallsRaws(
    id: CallsRawsId,
    orig: CallsRawsOrig,
    peerId: CallsRawsPeerId,
    cdrId: CallsRawsCdrId,
    connectTime: CallsRawsConnectTime,
    trunkId: CallsRawsTrunkId,
    clientId: CallsRawsClientId,
    serviceTrunkId: CallsRawsServiceTrunkId,
    serviceNumberId: CallsRawsServiceNumberId,
    srcNumber: CallsRawsSrcNumber,
    dstNumber: CallsRawsDstNumber,
    billedTime: CallsRawsBilledTime,
    rate: CallsRawsRate,
    cost: CallsRawsCost,
    pricelistId: CallsRawsPricelistId,
    disconnectCause: CallsRawsDisconnectCause
  )
}
