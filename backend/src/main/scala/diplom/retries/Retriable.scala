package com.mcn.diplom.retries

import derevo.cats.show
import derevo.derive
import fs2.io.file.Path

@derive(show)
sealed trait Retriable

object Retriable {

  case class RestPushs(msg: String)                      extends Retriable
  case class RobinBobinRestApiCall(msg: String)          extends Retriable

}
