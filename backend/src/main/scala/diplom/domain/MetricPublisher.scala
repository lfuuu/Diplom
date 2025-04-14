package com.mcn.diplom.domain

import scala.util.control.NoStackTrace

import derevo.cats._
import derevo.derive
import org.http4s.Status

object MetricPublisherResult {

  @derive(show)
  case class MetricPublisherResult(status: Status)
  case class MetricPublisherError(msg: String) extends NoStackTrace

  @derive(show)
  case class MetricPublisher(s: String)

}
