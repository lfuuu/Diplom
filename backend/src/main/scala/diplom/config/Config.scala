package com.mcn.diplom.config

import cats.effect.Async
import eu.timepit.refined.pureconfig._
import org.typelevel.log4cats.Logger
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._
import pureconfig.module.ip4s._

import types._

object Config {

  def load[F[_]: Async: Logger]: F[AppConfig] =
    ConfigSource.default.loadF[F, AppConfig]()

}
