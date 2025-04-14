package com.mcn.diplom.modules

import cats.effect.Temporal
import com.mcn.diplom.config.types._
import com.mcn.diplom.lib._
import org.typelevel.log4cats.Logger

object Programs {

  def make[F[_]: Background: Logger: Temporal](
    appConfig: AppConfig,
    services: Services[F],
    clients: HttpClients[F]
  ): Programs[F] =
    new Programs[F](appConfig, services, clients) {}
}

sealed abstract class Programs[F[_]: Background: Logger: Temporal] private (
  val cfg: AppConfig,
  val services: Services[F],
  val clients: HttpClients[F]
) {

  //val grayLogPusher: GrayLogPusher[F] = GrayLogPusher[F](cfg.grayLogPusherConfig)

}
