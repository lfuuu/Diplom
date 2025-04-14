package com.mcn.diplom.retries

import derevo.cats.show
import derevo.derive

@derive(show)
sealed trait Retriable

object Retriable {

  case class RestPushs(msg: String)             extends Retriable
  case class RobinBobinRestApiCall(msg: String) extends Retriable

}
