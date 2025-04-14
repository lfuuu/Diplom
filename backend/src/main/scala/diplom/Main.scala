package com.mcn.diplom

import cats.effect._
import cats.effect.std.Supervisor
import cats.syntax.all._
import com.mcn.diplom.modules._
import com.mcn.diplom.resources.{ AppResources, MkHttpServer }
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
              .evalMap { res =>
                Security.make[IO](cfg, res.postgres).map { security =>
                  val clients  = HttpClients.make[IO](cfg, res.client)
                  val services = Services.make[IO](cfg, res)
                  val programs = Programs.make[IO](cfg, services, clients)
                  val api      = HttpApi.make[IO](services, programs, res, security)
                  R
                  (cfg, api)
                }
              }
              .flatMap {
                case (cfg, api) =>
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
