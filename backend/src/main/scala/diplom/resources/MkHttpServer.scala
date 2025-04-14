package com.mcn.diplom.resources

import cats.effect.kernel.{ Async, Resource }
import com.mcn.diplom.config.types.HttpServerConfig
import fs2.io.net.Network
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.Logger

trait MkHttpServer[F[_]] {
  def newEmber(cfg: HttpServerConfig, httpApp: HttpApp[F]): Resource[F, Server]
}

object MkHttpServer {
  def apply[F[_]: MkHttpServer]: MkHttpServer[F] = implicitly

  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
    Logger[F].info(s"HTTP Server started at ${s.address}")

  implicit def forAsyncLogger[F[_]: Async: Network: Logger]: MkHttpServer[F] =
    new MkHttpServer[F] {

      def newEmber(cfg: HttpServerConfig, httpApp: HttpApp[F]): Resource[F, Server] =
        EmberServerBuilder
          .default[F]
          .withHost(cfg.host)
          .withPort(cfg.port)
          .withHttpApp(httpApp)
          .build
          .evalTap(showEmberBanner[F])
    }
}
