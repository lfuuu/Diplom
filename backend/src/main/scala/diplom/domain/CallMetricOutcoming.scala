package com.mcn.diplom.domain

import java.time.format.DateTimeFormatter
import java.time.{ Instant, LocalDateTime, ZoneId }

import scala.util.Try

import com.comcast.ip4s.Ipv4Address
import com.mcn.diplom.domain.CallMetricStateEnum._
import com.mcn.diplom.domain.misc._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.estatico.newtype.macros.newtype

object CallMetricOutcoming {

  val formatedDateTime = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss.SSSzzz")
    .withZone(ZoneId.of("UTC"))

  val formatedRadiusDateTime = DateTimeFormatter
    .ofPattern("HH:mm:ss.SSS zzz MMM dd yyyy")
    .withZone(ZoneId.of("UTC"))

  def parseToInstant(s: String) =
    Try {
      val localDateTime = LocalDateTime.parse(s, formatedRadiusDateTime);
      val zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"))
      zonedDateTime.toInstant
    }.toOption.getOrElse(Instant.now())

  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](x => formatedDateTime.format(x))

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingConnectTime(value: Instant)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingSetupTime(value: Instant)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingDisconnectTime(value: Instant)

  @derive(decoder)
  @newtype
  case class CallMetricOutcomingState(value: Code)

  implicit val callMetricOutcomingState: Encoder[CallMetricOutcomingState] = Encoder.instance {
    case STATE_START  => Json.fromString("call_start")
    case STATE_ANSWER => Json.fromString("call_answer")
    case STATE_END    => Json.fromString("call_end")
    case _            => Json.fromString("call_start") // уродски, пока не знаю как пристроить @unchecked
  }

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingRegionId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingUsType(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingNasIp(value: Ipv4Address)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingCalling(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingCalled(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingRedirecting(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingOCPN(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingInTrunk(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingOutTrunk(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingReleasingParty(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingDisconnectCause(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingXMCNpbxInfo(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingSessionTime(value: Long)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricOutcomingDirection(value: String)

  case class CallMetricOutcoming(
    state: CallMetricOutcomingState,
    regionId: CallMetricOutcomingRegionId,
    usType: CallMetricOutcomingUsType = CallMetricOutcomingUsType(1),
    nasIp: CallMetricOutcomingNasIp,
    callId: CallMetricOutcomingCallId,
    calling: CallMetricOutcomingCalling,
    called: CallMetricOutcomingCalled,
    inTrunk: CallMetricOutcomingInTrunk,
    redirecting: Option[CallMetricOutcomingRedirecting] = None,
    setupTime: Option[CallMetricOutcomingSetupTime] = None,
    connectTime: Option[CallMetricOutcomingConnectTime] = None,
    disconnectTime: Option[CallMetricOutcomingDisconnectTime] = None,
    outTrunk: Option[CallMetricOutcomingOutTrunk] = None,
    sessionTime: Option[CallMetricOutcomingSessionTime] = None,
    disconnectCause: Option[CallMetricOutcomingDisconnectCause] = None,
    releasingParty: Option[CallMetricOutcomingReleasingParty] = None,
    ocpn: Option[CallMetricOutcomingOCPN] = None,
    xMCNpbxInfo: Option[CallMetricOutcomingXMCNpbxInfo] = None,
    direction: Option[CallMetricOutcomingDirection] = None
  )

  implicit val encoderCallMetricOutcoming: Encoder[CallMetricOutcoming] = new Encoder[CallMetricOutcoming] {

    final def apply(a: CallMetricOutcoming): Json =
      Json
        .obj(
          ("state", a.state.asJson),
          ("region_id", a.regionId.value.asJson),
          ("us_type", a.usType.value.asJson),
          ("call_id", a.callId.asJson),
          ("nas_ip", a.nasIp.asJson),
          ("calling", a.calling.asJson),
          ("called", a.called.asJson),
          ("in_trunk", a.inTrunk.asJson),
          ("redirect", a.redirecting.asJson),
          ("setup_time", a.setupTime.asJson),
          ("connect_time", a.connectTime.asJson),
          ("disconnect_time", a.disconnectTime.asJson),
          ("out_trunk", a.outTrunk.asJson),
          ("session_time", a.sessionTime.map(_.value.toString).asJson),
          ("disconnect_cause", a.disconnectCause.asJson),
          ("releasing_party", a.releasingParty.asJson),
          ("ocpn", a.ocpn.asJson),
          ("direction", a.direction.map(_.value.toString).asJson),
          ("x_pbx_info", a.xMCNpbxInfo.flatMap(j => parse(j.value).toOption).asJson)
        )
        .deepDropNullValues
  }

}
