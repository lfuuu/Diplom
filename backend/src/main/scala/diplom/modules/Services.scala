package com.mcn.diplom.modules

import cats.effect._
import com.mcn.diplom.config.types._
import com.mcn.diplom.lib.GenUUID
import com.mcn.diplom.resources.AppResources
import org.typelevel.log4cats.Logger
import com.mcn.diplom.services.HealthCheck
import com.mcn.diplom.services.BillingClientsService

sealed abstract class Services[F[_]] private (
  val healthCheck: HealthCheck[F],
  val billingClients: BillingClientsService[F]
)

object Services {

  def make[F[_]: GenUUID: Logger: Async: Temporal](
    cfg: AppConfig,
    appResources: AppResources[F]
  ): Services[F] =
    new Services[F](
      healthCheck = HealthCheck.make[F](appResources.postgres),
      billingClients = BillingClientsService.make[F](appResources.postgres)
    ) {}
}
