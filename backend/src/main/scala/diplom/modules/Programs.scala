package com.mcn.diplom.modules

import cats.effect.Temporal
import com.mcn.diplom.config.types._
import com.mcn.diplom.lib._
import org.typelevel.log4cats.Logger
import com.mcn.diplom.programs.BillingCall

object Programs {

  def make[F[_]: Background: Logger: Temporal: Time](
    appConfig: AppConfig,
    services: Services[F],
    clients: HttpClients[F]
  ): Programs[F] =
    new Programs[F](appConfig, services, clients) {}
}

sealed abstract class Programs[F[_]: Background: Logger: Temporal: Time] private (
  val cfg: AppConfig,
  val services: Services[F],
  val clients: HttpClients[F]
) {

  val billingCall = new BillingCall(
    services.billingClients,
    services.authTrunksService,
    services.billingServiceNumbersService,
    services.billingServiceTrunksService,
    services.billingPacketsService,
    services.billingPricelistItemsService
  )
  //val grayLogPusher: GrayLogPusher[F] = GrayLogPusher[F](cfg.grayLogPusherConfig)

}
