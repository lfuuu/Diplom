package com.mcn.diplom.resources

import cats.Applicative
import cats.effect.kernel.Async
import cats.effect.std.Console
import cats.effect.{ Concurrent, Resource }
import cats.syntax.all._
import com.mcn.diplom.config.types._
import eu.timepit.refined.auto._
import fs2.io.net.Network
import natchez.Trace.Implicits.noop
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import skunk._
import skunk.codec.text._
import skunk.implicits._

sealed abstract class AppResources[F[_]](
  val client: Client[F],
  val postgres: Resource[F, Session[F]]
)

object AppResources {

  def make[F[_]: Async: Applicative: Concurrent: Console: Logger: MkHttpClient: Network](
    cfg: AppConfig
  ): Resource[F, AppResources[F]] = {

    def checkPostgresConnection(
      postgres: Resource[F, Session[F]]
    ): F[Unit] =
      postgres.use { session =>
        session
          .unique(sql"select version();".query(text))
          .flatMap(v => Logger[F].info(s"Connected to Postgres $v"))

      }

    def mkPostgreSqlResource(c: PostgreSQLConfig): SessionPool[F] =
      Session
        .pooled[F](
          host = c.host.value,
          port = c.port.value,
          user = c.user.value,
          password = Some(c.password.value),
          database = c.database.value,
          max = c.max.value
        )
    //.evalTap(checkPostgresConnection)

    for {
      client   <- MkHttpClient[F].newEmber(cfg.httpClientConfig)
      postgres <- mkPostgreSqlResource(cfg.postgreSQL)
    } yield new AppResources[F](
      client,
      postgres
    ) {}
  }

}
