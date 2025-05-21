package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

object AuthRequest {

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthRequestSrcNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthRequestDstNumber(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthRequestSrcRoute(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthRequestDstRoute(value: String)

  @derive(decoder, encoder, schema) case class AuthRequest(
    srcNumber: AuthRequestSrcNumber,
    dstNumber: AuthRequestDstNumber,
    srcRoute: AuthRequestSrcRoute
  )

}
