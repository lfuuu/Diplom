package com.mcn.diplom.domain.nispd

import scala.util.control.NoStackTrace

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import sttp.tapir.derevo.schema

object AuthRequestError {

  @derive(show, decoder, encoder)
  sealed trait AuthRequestError extends NoStackTrace {
    def cause: String
  }

  @derive(eqv, show, schema, decoder, encoder)
  case class AccessReject(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class RouteNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class SrcTrunkNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class TrunkNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class ServiceTrunkNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class NumberNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class ClientNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class ClientIsBlocked(cause: String) extends AuthRequestError


  @derive(eqv, show, schema, decoder, encoder)
  case class PricelistNotFound(cause: String) extends AuthRequestError

  @derive(eqv, show, schema, decoder, encoder)
  case class OrigLegBillError(cause: String) extends AuthRequestError

}
