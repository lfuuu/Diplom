package com.mcn.diplom.modules

import cats.effect.kernel.Async
import com.mcn.diplom.config.types._
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

object HttpClients {

  def make[F[_]: Logger: Async](
    cfg: AppConfig,
    client: Client[F]
  ): HttpClients[F] =
    new HttpClients[F] {
      //def metricPublishClient: MetricPublisherClient[F] = MetricPublisherClient.make[F](cfg.MetricPublisherConfig, client)
    }
}

sealed trait HttpClients[F[_]] {
  //def rwHookClient: RwHookClient[F]
}
