package com.mcn.diplom.lib.auth

import cats.effect.Sync
import cats.syntax.all._
import com.mcn.diplom.config.types.TokenExpiration
import com.mcn.diplom.lib.JwtClock
import pdi.jwt.JwtClaim

trait JwtExpire[F[_]] {
  def expiresIn(claim: JwtClaim, exp: TokenExpiration): F[JwtClaim]
  def isValid(claim: JwtClaim): F[Boolean]
}

object JwtExpire {

  def make[F[_]: Sync]: F[JwtExpire[F]] =
    JwtClock[F].utc.map { implicit jClock =>
      new JwtExpire[F] {
        def expiresIn(claim: JwtClaim, exp: TokenExpiration): F[JwtClaim] =
          Sync[F].delay(claim.issuedNow.expiresIn(exp.value.toSeconds))

        def isValid(claim: JwtClaim): F[Boolean] =
          Sync[F].delay(claim.isValid)
      }
    }
}
