package com.mcn.diplom.domain.nispd

import scala.util.control.NoStackTrace

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import sttp.tapir.derevo.schema

object BillingCallError {

  @derive(show)
  sealed trait BillingCallError extends NoStackTrace {
    def cause: String
  }

  @derive(eqv, show, schema, decoder, encoder)
  case class TrunkNotFound(cause: String) extends BillingCallError

  @derive(eqv, show, schema, decoder, encoder)
  case class ServiceTrunkNotFound(cause: String) extends BillingCallError

  @derive(eqv, show, schema, decoder, encoder)
  case class NumberNotFound(cause: String) extends BillingCallError

  @derive(eqv, show, schema, decoder, encoder)
  case class ClientNotFound(cause: String) extends BillingCallError

}
