package com.mcn.diplom.domain.nispd

import derevo.cats._
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
  case class BillingServiceTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceNumberId(value: Int)

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
  case class BillingPricelistId(value: Int)

  @derive(decoder, encoder, schema)
  case class BillingPacketCreateRequest(
    serviceTrunkId: BillingServiceTrunkId,
    serviceNumberId: BillingServiceNumberId,
    activationDt: BillingPacketActivationDt,
    expireDt: Option[BillingPacketExpireDt],
    orig: BillingPacketOrig,
    pricelistId: BillingPricelistId
  )

  @derive(decoder, encoder, schema)
  case class BillingPacket(
    id: BillingPacketId,
    serviceTrunkId: BillingServiceTrunkId,
    serviceNumberId: BillingServiceNumberId,
    activationDt: BillingPacketActivationDt,
    expireDt: Option[BillingPacketExpireDt],
    orig: BillingPacketOrig,
    pricelistId: BillingPricelistId
  )
}
