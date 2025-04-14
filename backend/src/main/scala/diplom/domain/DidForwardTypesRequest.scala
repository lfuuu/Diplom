package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema

@derive(schema, decoder, encoder)
case class DidForwartTypesRequest(
  did: DidForwartTypesRequest.DidForwartTypesRequestNumber
)

object DidForwartTypesRequest {

  @derive(decoder, encoder)
  @newtype
  case class DidForwartTypesRequestNumber(value: String)

}
