package com.mcn.diplom.domain.nispd

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

object AuthResponse {

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthResponseDstRoute(value: String)

  @derive(decoder, encoder, schema) case class AuthResponse(
    dstRoute: AuthResponseDstRoute
  )

}
