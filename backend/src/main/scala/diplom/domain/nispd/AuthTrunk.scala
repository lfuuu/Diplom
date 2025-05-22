package com.mcn.diplom.domain.auth

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

object AuthTrunk {

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthTrunkId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthTrunkName(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthTrunkAuthByNumber(value: Boolean)

  @derive(decoder, encoder, schema)
  case class AuthTrunkCreateRequest(
    trunkName: AuthTrunkName,
    authByNumber: AuthTrunkAuthByNumber
  )

  @derive(decoder, encoder, schema)
  case class AuthTrunk(
    id: AuthTrunkId,
    trunkName: AuthTrunkName,
    authByNumber: AuthTrunkAuthByNumber
  )
}
