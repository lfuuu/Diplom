package com.mcn.diplom.domain

import scala.util.control.NoStackTrace

import derevo.cats._
import derevo.derive
import org.http4s.Status

object RestPusher {

  @derive(show)
  case class RestPushResult(status: Status)
  case class RestPushError(msg: String) extends NoStackTrace

  @derive(show)
  case class RestPush(s: String)

}
