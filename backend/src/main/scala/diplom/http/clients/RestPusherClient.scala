package com.mcn.diplom.http.clients

import cats.MonadThrow
import cats.effect.Temporal
import cats.syntax.all._
import com.mcn.diplom.config.types.RestPusherConfig
import com.mcn.diplom.domain.RestPusher._
import com.mcn.diplom.retries.{ Retriable, Retry }
import eu.timepit.refined.auto._
import io.circe.Encoder
import org.http4s.Method._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.typelevel.log4cats.Logger
import retry.RetryPolicy

trait RestPusherClient[F[_]] {
  def push[T](item: T)(implicit encoder: Encoder[T]): F[RestPushResult]
  def reliablePush[T](item: T, policy: RetryPolicy[F])(implicit encoder: Encoder[T]): F[RestPushResult]
}

object RestPusherClient {

  def make[F[_]: Logger: Temporal](
    cfg: RestPusherConfig,
    client: Client[F]
  ): RestPusherClient[F] =
    new RestPusherClient[F] with Http4sClientDsl[F] {

      def reliablePush[T](item: T, policy: RetryPolicy[F])(implicit encoder: Encoder[T]): F[RestPushResult] =
        Retry[F]
          .retry(policy, Retriable.RestPushs(s" ошибка вызова rest-api: ${cfg.uri.value}"))(push(item))
          .adaptError {
            case e =>
              RestPushError(Option(e.getMessage).getOrElse("Unknown"))
          }

      def push[T](item: T)(implicit encoder: Encoder[T]): F[RestPushResult] =
        Uri.fromString(cfg.uri.value).liftTo[F].flatMap { uri =>
          client.run(POST(item, uri)).use { resp =>
            resp.status match {
              case st: Status if st.isSuccess => RestPushResult(st).pure[F]
              case st                         => MonadThrow[F].raiseError(RestPushError(s"#${st.code}:${st.reason}"))
            }
          }
        }
    }
}
