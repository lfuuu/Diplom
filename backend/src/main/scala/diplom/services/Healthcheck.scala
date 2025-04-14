package com.mcn.diplom.services

import scala.concurrent.duration._

import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._
import com.mcn.diplom.domain.healthcheck._
import skunk._
import skunk.codec.all._
import skunk.implicits._

trait HealthCheck[F[_]] {
  def status: F[AppStatus]
}

object HealthCheck {

  def make[F[_]: Temporal](
    postgres: Resource[F, Session[F]]
  ): HealthCheck[F] =
    new HealthCheck[F] {

      val q: Query[Void, Int] =
        sql"SELECT pid FROM pg_stat_activity".query(int4)

      val postgresHealth: F[PostgresStatus] =
        postgres
          .use(_.execute(q))
          .map(_.nonEmpty)
          .timeout(1.second)
          .map(Status._Bool.reverseGet)
          .orElse(Status.Unreachable.pure[F].widen)
          .map(PostgresStatus.apply)

      val status: F[AppStatus] =
        postgresHealth.map(AppStatus.apply)
    }

}
