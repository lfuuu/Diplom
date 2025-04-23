package com.mcn.diplom.domain.auth

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

object AuthUser {

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthUserId(value: Long)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthClientId(value: Int)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthUserLogin(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthUserPassword(value: String)

  @derive(decoder, encoder, schema)
  @newtype
  case class AuthUserActive(value: Boolean)

  @derive(decoder, encoder, schema)
  case class AuthUserCreateRequest(
    clientId: AuthClientId,
    login: AuthUserLogin,
    password: AuthUserPassword,
    active: AuthUserActive
  )

  @derive(decoder, encoder, schema)
  case class AuthUser(
    id: AuthUserId,
    clientId: AuthClientId,
    login: AuthUserLogin,
    password: AuthUserPassword,
    active: AuthUserActive
  )
}
