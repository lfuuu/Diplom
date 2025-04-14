package com.mcn.diplom.services

import cats.effect._
import cats.syntax.all._
import com.mcn.diplom.domain.Auth._
import com.mcn.diplom.http.auth.users._
import com.mcn.diplom.lib.{ GenUUID, ID }
import com.mcn.diplom.sql.codecs._
import skunk._
import skunk.implicits._

trait Users[F[_]] {
  def find(username: UserName): F[Option[UserWithPassword]]
  def create(username: UserName, password: EncryptedPassword): F[UserId]
}

object Users {

  def make[F[_]: GenUUID: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): Users[F] =
    new Users[F] {
      import UserSQL._

      def find(username: UserName): F[Option[UserWithPassword]] =
        postgres.use { session =>
          session.prepare(selectUser).flatMap { q =>
            q.option(username).map {
              case Some(u *: p *: EmptyTuple) => UserWithPassword(u.id, u.name, p).some
              case _                          => none[UserWithPassword]
            }
          }
        }

      def create(username: UserName, password: EncryptedPassword): F[UserId] =
        postgres.use { session =>
          session.prepare(insertUser).flatMap { cmd =>
            ID.make[F, UserId].flatMap { id =>
              cmd
                .execute(User(id, username) *: password *: EmptyTuple)
                .as(id)
                .recoverWith {
                  case SqlState.UniqueViolation(_) =>
                    UserNameInUse(username).raiseError[F, UserId]
                }
            }
          }
        }
    }

}

private object UserSQL {

  val codec: Codec[User *: EncryptedPassword *: EmptyTuple] =
    (userId *: userName *: encPassword).imap {
      case i *: n *: p =>
        User(i, n) *: p
    } {
      case u *: p =>
        u.id *: u.name *: p
    }

  val selectUser: Query[UserName, User *: EncryptedPassword *: EmptyTuple] =
    sql"""
        SELECT id, name, enc_password FROM users
        WHERE name = $userName
       """.query(codec)

  val insertUser: Command[User *: EncryptedPassword *: EmptyTuple] =
    sql"""
        INSERT INTO users (id, name, enc_password)
        VALUES ($codec)
        """.command

}
