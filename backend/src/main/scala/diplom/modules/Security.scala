package com.mcn.diplom.modules

import cats.ApplicativeThrow
import cats.effect._
import cats.syntax.all._
import com.mcn.diplom.config.types._
import com.mcn.diplom.domain.Auth._
import com.mcn.diplom.http.auth.users._
import com.mcn.diplom.lib.auth._
import com.mcn.diplom.services._
import dev.profunktor.auth.jwt._
import io.circe.parser.{ decode => jsonDecode }
import org.typelevel.log4cats.Logger
import pdi.jwt._
import skunk.Session

object Security {

  def make[F[_]: Sync: Logger](
    cfg: AppConfig,
    postgres: Resource[F, Session[F]]
//      redis: RedisCommands[F, String, String]
  ): F[Security[F]] = {

    val adminJwtAuth: AdminJwtAuth =
      AdminJwtAuth(
        JwtAuth
          .hmac(
            cfg.adminJwtConfig.secretKey.secret.value,
            JwtAlgorithm.HS256
          )
      )

    val userJwtAuth: UserJwtAuth =
      UserJwtAuth(
        JwtAuth
          .hmac(
            cfg.tokenConfig.secret.value,
            JwtAlgorithm.HS256
          )
      )

    val adminToken = JwtToken(cfg.adminJwtConfig.adminToken.secret.value)

    for {
      adminClaim <- jwtDecode[F](adminToken, adminJwtAuth.value)
      content    <- ApplicativeThrow[F].fromEither(jsonDecode[ClaimContent](adminClaim.content))
      adminUser   = AdminUser(User(content.id, UserName("admin")))
      tokens     <- JwtExpire.make[F].map(Tokens.make[F](_, cfg.tokenConfig, cfg.tokenExpiration))
      crypto     <- Crypto.make[F](cfg.passwordSalt)
      users       = Users.make[F](postgres)
      auth        = Auth.make[F](cfg.tokenExpiration, tokens, users, crypto)
      adminAuth   = UsersAuth.admin[F](adminToken, adminUser)
      usersAuth   = UsersAuth.common[F](tokens)
    } yield new Security[F](auth, adminAuth, usersAuth, adminJwtAuth, userJwtAuth) {}

  }
}

sealed abstract class Security[F[_]] private (
  val auth: Auth[F],
  val adminAuth: UsersAuth[F, AdminUser],
  val usersAuth: UsersAuth[F, CommonUser],
  val adminJwtAuth: AdminJwtAuth,
  val userJwtAuth: UserJwtAuth
)
