package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.Instant
import sttp.tapir.derevo.schema

object BillingServiceTrunk {

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingClientId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceTrunkActivationDt(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceTrunkExpireDt(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceTrunkOrigEnabled(value: Boolean)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceTrunkTermEnabled(value: Boolean)

  @derive(decoder, encoder, schema)
  case class BillingServiceTrunkCreateRequest(
    clientId: BillingClientId,
    trunkId: BillingTrunkId,
    activationDt: BillingServiceTrunkActivationDt,
    expireDt: Option[BillingServiceTrunkExpireDt],
    origEnabled: BillingServiceTrunkOrigEnabled,
    termEnabled: BillingServiceTrunkTermEnabled
  )

  @derive(decoder, encoder, schema)
  case class BillingServiceTrunk(
    id: BillingServiceTrunkId,
    clientId: BillingClientId,
    trunkId: BillingTrunkId,
    activationDt: BillingServiceTrunkActivationDt,
    expireDt: Option[BillingServiceTrunkExpireDt],
    origEnabled: BillingServiceTrunkOrigEnabled,
    termEnabled: BillingServiceTrunkTermEnabled
  )
}
