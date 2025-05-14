package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.Instant
import sttp.tapir.derevo.schema

object BillingServiceNumber {

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceNumberId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceNumberClientId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceNumberDID(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceNumberActivationDt(value: Instant)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingServiceNumberExpireDt(value: Instant)

  @derive(decoder, encoder, schema)
  case class BillingServiceNumberCreateRequest(
    clientId: BillingServiceNumberClientId,
    did: BillingServiceNumberDID,
    activationDt: BillingServiceNumberActivationDt,
    expireDt: Option[BillingServiceNumberExpireDt]
  )

  @derive(decoder, encoder, schema)
  case class BillingServiceNumber(
    id: BillingServiceNumberId,
    clientId: BillingServiceNumberClientId,
    did: BillingServiceNumberDID,
    activationDt: BillingServiceNumberActivationDt,
    expireDt: Option[BillingServiceNumberExpireDt]
  )
}
