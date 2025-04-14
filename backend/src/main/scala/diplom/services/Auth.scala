package com.mcn.diplom.services

import cats._
import cats.effect.kernel.Sync
import cats.syntax.all._
import com.mcn.diplom.config.types.TokenExpiration
import com.mcn.diplom.domain.Auth._
import com.mcn.diplom.domain.InvalidClaimContent
import com.mcn.diplom.http.auth.users._
import com.mcn.diplom.lib.GenUUID
import com.mcn.diplom.lib.auth.{ Crypto, Tokens }
import com.mcn.diplom.services.Users
import dev.profunktor.auth.jwt.JwtToken
import io.circe.parser.decode
import org.typelevel.log4cats.Logger
import pdi.jwt.JwtClaim

trait Auth[F[_]] {
  def newUser(username: UserName, password: Password): F[JwtToken]
  def login(username: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, username: UserName): F[Unit]
}

trait UsersAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}

object UsersAuth {

  def admin[F[_]: Applicative: Logger](
    adminToken: JwtToken,
    adminUser: AdminUser
  ): UsersAuth[F, AdminUser] =
    new UsersAuth[F, AdminUser] {

      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[AdminUser]] =
        (token === adminToken)
          .guard[Option]
          .as(adminUser)
          .pure[F]
    }

  def common[F[_]: Sync: Functor: GenUUID: Logger](tokens: Tokens[F]): UsersAuth[F, CommonUser] =
    new UsersAuth[F, CommonUser] {

      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[CommonUser]] =
        for {
          cu   <- tokens.isValidO(claim).map(_.map(c => decode[ClaimContent](c.content)))
          user <- cu match {
                    case Some(Right(u)) => CommonUser(User(u.id, u.username)).some.pure[F]
                    case _              => InvalidClaimContent().raiseError
                  }
        } yield user
    }

}

object Auth {

  def make[F[_]: MonadThrow: Sync: Logger](
    tokenExpiration: TokenExpiration,
    tokens: Tokens[F],
    users: Users[F],
    crypto: Crypto
  ): Auth[F] =
    new Auth[F] {

      private val TokenExpiration = tokenExpiration.value

      def newUser(username: UserName, password: Password): F[JwtToken] =
        users.find(username).flatMap {
          case Some(_) => UserNameInUse(username).raiseError[F, JwtToken]
          case None    =>
            for {
              i <- users.create(username, crypto.hashMd5(password))
              c  = ClaimContent(i, username)
              t <- tokens.create(c)
            } yield t
        }

      def login(username: UserName, password: Password): F[JwtToken] =
        users.find(username).flatMap {
          case None                                                     => UserNotFound(username).raiseError[F, JwtToken]
          case Some(user) if user.password =!= crypto.hashMd5(password) =>
            InvalidPassword(user.name).raiseError[F, JwtToken]
          case Some(user)                                               => tokens.create(ClaimContent(user.id, user.name))
        }

      def logout(token: JwtToken, username: UserName): F[Unit] = Applicative[F].unit

    }
}
