package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.time.LocalDate
import sttp.tapir.derevo.schema

object BillingPricelist {

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistName(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistDateFrom(value: LocalDate)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistDateTo(value: LocalDate)

  @derive(decoder, encoder, schema)
  @newtype
  case class BillingPricelistRoundToSec(value: Boolean)

  @derive(decoder, encoder, schema)
  case class BillingPricelistCreateRequest(
    name: BillingPricelistName,
    dateFrom: BillingPricelistDateFrom,
    dateTo: Option[BillingPricelistDateTo],
    roundToSec: BillingPricelistRoundToSec
  )

  @derive(decoder, encoder, schema)
  case class BillingPricelist(
    id: BillingPricelistId,
    name: BillingPricelistName,
    dateFrom: BillingPricelistDateFrom,
    dateTo: Option[BillingPricelistDateTo],
    roundToSec: BillingPricelistRoundToSec
  )
}
