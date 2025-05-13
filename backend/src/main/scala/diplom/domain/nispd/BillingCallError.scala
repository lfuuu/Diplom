package com.mcn.diplom.domain.nispd

import scala.util.control.NoStackTrace

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money



object BillingCallError {

@derive(show)
sealed trait BillingCallError extends NoStackTrace {
  def cause: String
}

  @derive(eqv, show)
  case class TrunkNotFound(cause: String)        extends BillingCallError
  case class ServiceTrunkNotFound(cause: String) extends BillingCallError
}
