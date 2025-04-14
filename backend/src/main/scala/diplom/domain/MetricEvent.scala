package com.mcn.diplom.domain

import com.mcn.diplom.domain.CallMetricIncoming.CallMetricIncoming
import com.mcn.diplom.domain.TransitCallMetricIncoming._
import com.mcn.diplom.domain.misc.Timestamp

sealed trait MetricEvent extends Product with Serializable {
  val dt: Timestamp
}

case class AuthTransitMetricEvent(dt: Timestamp, transitCall: TransitCallMetricIncoming) extends MetricEvent
case class AuthMetricEvent(dt: Timestamp, auth: CallMetricIncoming)                      extends MetricEvent
case class StartMetricEvent(dt: Timestamp, startAcc: Cdr)                                extends MetricEvent
case class StopMetricEvent(dt: Timestamp, stopAcc: Cdr)                                  extends MetricEvent
case class ShutUpEvent(dt: Timestamp)                                                    extends MetricEvent
