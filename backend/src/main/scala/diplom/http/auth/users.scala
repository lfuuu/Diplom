package com.mcn.diplom.http.auth

import com.mcn.diplom.domain.Auth._
import derevo.cats.show
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import dev.profunktor.auth.jwt._
import io.estatico.newtype.macros.newtype

object users {

  @newtype case class AdminJwtAuth(value: JwtSymmetricAuth)
  @newtype case class UserJwtAuth(value: JwtSymmetricAuth)

  @derive(decoder, encoder, show)
  case class User(id: UserId, name: UserName)

  @derive(decoder, encoder)
  case class UserWithPassword(id: UserId, name: UserName, password: EncryptedPassword)

  @derive(show, decoder, encoder)
  @newtype
  case class CommonUser(value: User)

  @derive(show)
  @newtype
  case class AdminUser(value: User)

}
