package com.mcn.diplom.modules

import cats.effect._
import com.mcn.diplom.config.types._
import com.mcn.diplom.lib.GenUUID
import com.mcn.diplom.resources.AppResources
import org.typelevel.log4cats.Logger
import com.mcn.diplom.services.HealthCheck
import com.mcn.diplom.services.BillingClientsService
import com.mcn.diplom.services.BillingPacketsService
import com.mcn.diplom.services.BillingServiceNumbersService
import com.mcn.diplom.services.BillingServiceTrunksService

sealed abstract class Services[F[_]] private (
  val healthCheck: HealthCheck[F],
  val billingClients: BillingClientsService[F],
  val billingPacketsService: BillingPacketsService[F],
  val billingServiceNumbersService: BillingServiceNumbersService[F],
  val billingServiceTrunksService: BillingServiceTrunksService[F]
)

object Services {

  def make[F[_]: GenUUID: Logger: Async: Temporal](
    cfg: AppConfig,
    appResources: AppResources[F]
  ): Services[F] =
    new Services[F](
      healthCheck = HealthCheck.make[F](appResources.postgres),
      billingClients = BillingClientsService.make[F](appResources.postgres),
      billingPacketsService = BillingPacketsService.make[F](appResources.postgres),
      billingServiceNumbersService = BillingServiceNumbersService.make[F](appResources.postgres),
      billingServiceTrunksService = BillingServiceTrunksService.make[F](appResources.postgres)
    ) {}
}
