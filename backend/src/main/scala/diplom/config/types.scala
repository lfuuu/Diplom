package com.mcn.diplom.config

import scala.concurrent.duration._

import cats.Show
import cats.syntax.all._
import ciris._
import com.comcast.ip4s.{ Host, Port }
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import pureconfig.ConfigReader
import pureconfig.error._
import pureconfig.generic.semiauto._

object types {


  case class AdminUserTokenConfig(secret: NonEmptyString)

  case class JwtSecretKeyConfig(secret: NonEmptyString)

  case class JwtAccessTokenKeyConfig(secret: NonEmptyString)

  case class JwtClaimConfig(secret: NonEmptyString)

  case class PasswordSalt(secret: NonEmptyString)

  @newtype
  case class TokenExpiration(value: FiniteDuration)

  implicit val tokenExpirationConfigReader: ConfigReader[TokenExpiration] =
    ConfigReader[FiniteDuration].map(TokenExpiration(_))


  case class AdminJwtConfig(
    secretKey: JwtSecretKeyConfig,
    claimStr: JwtClaimConfig,
    adminToken: AdminUserTokenConfig
  )


  case class AppConfig(
    adminJwtConfig: AdminJwtConfig,
    tokenConfig: JwtAccessTokenKeyConfig,
    passwordSalt: PasswordSalt,
    tokenExpiration: TokenExpiration,
    httpClientConfig: HttpClientConfig,
    postgreSQL: PostgreSQLConfig,
    httpServerConfig: HttpServerConfig,
  )

  case class PostgreSQLConfig(
    host: NonEmptyString,
    port: UserPortNumber,
    user: NonEmptyString,
    password: Secret[NonEmptyString],
    database: NonEmptyString,
    max: PosInt
  )

  case class HttpServerConfig(
    host: Host,
    port: Port
  )

  case class HttpClientConfig(
    timeout: FiniteDuration,
    idleTimeInPool: FiniteDuration
  )


  implicit val SecretShow: Show[NonEmptyString] = Show.show(_.value)

  implicit val SecretReader: ConfigReader[Secret[NonEmptyString]] =
    ConfigReader
      .fromNonEmptyString(n =>
        NonEmptyString
          .from(n)
          .leftMap(x => UserValidationFailed(s"не могу конвертировать [$n] в поле Secret[NonEmptyString]"))
      )
      .map(Secret(_))

}
