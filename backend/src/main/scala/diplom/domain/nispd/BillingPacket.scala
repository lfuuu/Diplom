package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.Instant
import sttp.tapir.derevo.schema

object BillingPacket {

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketServiceTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketServiceNumberId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketActivationDt(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketExpireDt(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketOrig(value: Boolean)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPacketPricelistId(value: Int)

  @derive(decoder, encoder, schema)
  case class BillingPacketCreateRequest(
    serviceTrunkId: BillingPacketServiceTrunkId,
    serviceNumberId: BillingPacketServiceNumberId,
    activationDt: BillingPacketActivationDt,
    expireDt: Option[BillingPacketExpireDt],
    orig: BillingPacketOrig,
    pricelistId: BillingPacketPricelistId
  )

  @derive(decoder, encoder, schema)
  case class BillingPacket(
    id: BillingPacketId,
    serviceTrunkId: BillingPacketServiceTrunkId,
    serviceNumberId: BillingPacketServiceNumberId,
    activationDt: BillingPacketActivationDt,
    expireDt: Option[BillingPacketExpireDt],
    orig: BillingPacketOrig,
    pricelistId: BillingPacketPricelistId
  )
}
