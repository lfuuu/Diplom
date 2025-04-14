package com.mcn.diplom.lib.auth

import cats.Monad
import cats.syntax.all._
import com.mcn.diplom.config.types._
import com.mcn.diplom.domain.Auth.ClaimContent
import com.mcn.diplom.lib.GenUUID
import dev.profunktor.auth.jwt._
import io.circe.syntax._
import org.typelevel.log4cats.Logger
import pdi.jwt._

trait Tokens[F[_]] {
  def create: F[JwtToken]
  def create(claimContent: ClaimContent): F[JwtToken]
  def isValidO(claim: JwtClaim): F[Option[JwtClaim]]
}

object Tokens {

  def make[F[_]: GenUUID: Monad: Logger](
    jwtExpire: JwtExpire[F],
    config: JwtAccessTokenKeyConfig,
    exp: TokenExpiration
  ): Tokens[F] =
    new Tokens[F] {

      def create: F[JwtToken] =
        for {
          uuid     <- GenUUID[F].make
          claim    <- jwtExpire.expiresIn(JwtClaim(uuid.asJson.noSpaces), exp)
          secretKey = JwtSecretKey(config.secret.value)
          token    <- jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
        } yield token

      def create(claimContent: ClaimContent): F[JwtToken] =
        for {
          claim    <- jwtExpire.expiresIn(JwtClaim(claimContent.asJson.noSpaces), exp)
          secretKey = JwtSecretKey(config.secret.value)
          token    <- jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
        } yield token

      def isValidO(claim: JwtClaim): F[Option[JwtClaim]] = jwtExpire.isValid(claim).map(i => if (i) claim.some else None)
    }
}
