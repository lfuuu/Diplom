package com.mcn.diplom.domain.nispd

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype

import java.time.Instant

object BillingClient {

  @derive(decoder, encoder)
  @newtype
  case class BillingClientId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class BillingClientDt(value: Instant)

  @derive(decoder, encoder)
  @newtype
  case class BillingClientBalance(value: BigDecimal)

  @derive(decoder, encoder)
  @newtype
  case class BillingClientIsBlocked(value: Boolean)

  @derive(decoder, encoder)
  @newtype
  case class BillingClientName(value: String)

  @derive(decoder, encoder)
  case class BillingClientCreateRequest(
    balance: BillingClientBalance,
    isBlocked: BillingClientIsBlocked,
    name: BillingClientName
  )

  @derive(decoder, encoder)
  case class BillingClient(
    id: BillingClientId,
    dtCreate: BillingClientDt,
    balance: BillingClientBalance,
    isBlocked: BillingClientIsBlocked,
    name: BillingClientName
  )

}
