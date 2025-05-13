package com.mcn.diplom.programs

import cats.MonadThrow
import org.typelevel.log4cats.Logger

final case class BillingCall[F[_]: Logger: MonadThrow](
) {}
