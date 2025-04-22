package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.LocalDate
import sttp.tapir.derevo.schema

object BillingPricelistItem {

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistItemId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPriceNdefId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPriceDateFrom(value: LocalDate)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPriceDateTo(value: LocalDate)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPrice(value: BigDecimal)

  @derive(decoder, encoder, schema)
  case class BillingPricelistItemCreateRequest(
    pricelistId: BillingPricelistId,
    ndef: BillingPriceNdefId,
    dateFrom: BillingPriceDateFrom,
    dateTo: Option[BillingPriceDateTo],
    price: BillingPrice
  )

  @derive(decoder, encoder, schema)
  case class BillingPricelistItem(
    id: BillingPricelistItemId,
    pricelistId: BillingPricelistId,
    ndef: BillingPriceNdefId,
    dateFrom: BillingPriceDateFrom,
    dateTo: Option[BillingPriceDateTo],
    price: BillingPrice
  )
}
