package com.mcn.diplom.modules

import cats.effect._
import com.mcn.diplom.config.types._
import com.mcn.diplom.domain.Cdr
import com.mcn.diplom.lib.GenUUID
import com.mcn.diplom.resources.AppResources
import com.mcn.diplom.services.cdrInserter.CdrInserterService
import com.mcn.diplom.services.dvoConsumer._
import com.mcn.diplom.services.metricCollector.MetricCollectorService
import com.mcn.diplom.services.{ CdrStorage, _ }
import org.typelevel.log4cats.Logger
import com.mcn.diplom.services.mcnAdapter.DidForwardTypesRequestFromVpbx

sealed abstract class Services[F[_]] private (
  val healthCheck: HealthCheck[F],
  val cdrStorage: CdrStorage[F]
)

object Services {

  def make[F[_]: GenUUID: Logger: Async: Temporal](
    cfg: AppConfig,
    appResources: AppResources[F]
  ): Services[F] =
    new Services[F](
      healthCheck = HealthCheck.make[F](appResources.postgres),
      cdrStorage = CdrStorage.make[F](appResources.postgres)
    ) {}
}
