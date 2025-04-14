package com.mcn.diplom

import _root_.acc2.services.dvoConsumer.VpbxEventModule
import cats.effect._
import cats.effect.std.Supervisor
import cats.syntax.all._
import com.mcn.diplom.domain.Cdr
import com.mcn.diplom.lib.PidFile
import com.mcn.diplom.modules._
import com.mcn.diplom.resources.{ AppResources, MkHttpServer }
import com.mcn.diplom.services.cdrInserter.{ CdrInserterModule, MkChunkInserter }
import com.mcn.diplom.services.dvoConsumer.MkDVOConsumer
import com.mcn.diplom.services.metricCollector.{ MetricCollectorModule, MkMetricCollector }
import com.mcn.diplom.services.radiusAccounting.{ MkRadiusServer, RadiusAccountingModule }
import com.mcn.diplom.services.transitMetricCollector.{ MkTransitCallCollector, TransitCallCollectorModule }
import fs2.io.file.Path
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{ Logger, SelfAwareStructuredLogger }

import config.Config

object Main extends IOApp.Simple {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] =
    Config.load[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config") >>
        Supervisor[IO]
          .use { implicit sp =>
            AppResources
              .make[IO](cfg)
              .evalTap(res => PidFile.make[IO].create(Path(cfg.pidFile)))
              .evalMap { res =>
                Security.make[IO](cfg, res.postgres).map { security =>
                  val clients          = HttpClients.make[IO](cfg, res.client)
                  val services         = Services.make[IO](cfg, res)
                  val programs         = Programs.make[IO](cfg, services, clients)
                  val api              = HttpApi.make[IO](services, programs, res, security)
                  val radiusAccounting =
                    RadiusAccountingModule.make[IO](cfg, services, res.client, res.postgres, res.md5Heaher, res.prometheusMetrics.radiusServerCounter)

                  val cdrChunkInserter =
                    CdrInserterModule.make[IO, Cdr](
                      cfg,
                      services.cdrStorage,
                      res.cdrChunkInserterQuery,
                      res.prometheusMetrics.inserterCdrGauge,
                      res.prometheusMetrics.inserterCdrCounter
                    )

                  val metricTransmitter =
                    MetricCollectorModule.make[IO](
                      cfg,
                      services,
                      res.client,
                      res.postgres,
                      res.callMetricIncomingQuery,
                      res.prometheusMetrics.publisherMetricQuerySizeGauge,
                      res.prometheusMetrics.publisherMetricCounter,
                      res.prometheusMetrics.authCallDelayBufferSizeGauge
                    )

                  val transitCallCollectorModule =
                    TransitCallCollectorModule.make[IO](
                      cfg,
                      services,
                      res.client,
                      res.postgres,
                      res.transitMetricIncomingQuery,
                      res.prometheusMetrics.transitCallCollectorQuerySizeGauge,
                      res.prometheusMetrics.transitCallCollectorCounter,
                      res.prometheusMetrics.authTransitCallDelayBufferSizeGauge
                    )

                  val vpbxEventModule =
                    VpbxEventModule.make[IO](
                      cfg,
                      services,
                      res.client,
                      res.prometheusMetrics.dvoEventPushCounter,
                      cfg.dvoEventProcessorConfig,
                      res.prometheusMetrics.dvoEventConsumeCounter
                    )

                  (cfg, api, radiusAccounting, cdrChunkInserter, metricTransmitter, transitCallCollectorModule, vpbxEventModule)
                }
              }
              .flatMap {
                case (cfg, api, radiusAccounting, cdrChunkInserter, metricTransmitter, transitCallCollectorModule, vpbxEventModule) =>
                  (
                    MkHttpServer[IO].newEmber(cfg.httpServerConfig, api.httpApp),
                    MkRadiusServer.make[IO](radiusAccounting),
                    MkChunkInserter.make[IO](cdrChunkInserter),
                    MkMetricCollector.make[IO](cfg.metricTransmitter, metricTransmitter),
                    MkDVOConsumer.make[IO](cfg.kafkaBrokerConfig, vpbxEventModule),
                    MkTransitCallCollector.make[IO](cfg.transitCallCollectorConfig, transitCallCollectorModule)
                  ).parTupled
              }
              .useForever

          }
          .handleErrorWith(x => Logger[IO].error(s"${x.getClass().toString} - ${x.getMessage}"))
    }

}
