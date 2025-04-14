package com.mcn.diplom.domain

import java.time.Instant

import cats.syntax.all._
import com.comcast.ip4s.Ipv4Address
import com.mcn.diplom.domain.CallMetricOutcoming._
import com.mcn.diplom.domain.CallMetricStateEnum._
import com.mcn.diplom.domain.misc._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema
import sttp.tapir.{ Schema, _ }
import Schema.annotations._
import ExtEventReciever._

@derive(schema, decoder, encoder)
case class CallMetricKey(
  nasIp: CallMetricIncoming.CallMetricIncomingNasIp,
  callId: CallMetricIncoming.CallMetricIncomingCallId
)

object CallMetricKey {
  def fromCallMetricIncoming(metric: CallMetricIncoming.CallMetricIncoming) = CallMetricKey(metric.nasIp, metric.callId)

  def fromCdr(cdr: Cdr) =
    CallMetricKey(CallMetricIncoming.CallMetricIncomingNasIp(cdr.nasIp.value), CallMetricIncoming.CallMetricIncomingCallId(cdr.callId.value.toString))
}

object CallMetricIncoming {

  @derive(schema, decoder, encoder)
  @description("событие метрики звонка")
  case class CallMetricIncoming(
    @description("время события")
    dt: CallMetricIncomingDt,
    @description("тип события.")
    state: CallMetricIncomingState,
    @description("Регион вызова")
    @validate(Validator.min(1).and(Validator.max(1199)))
    regionId: CallMetricIncomingRegionId,
    @description("ip-адрес коммутатора, четыре цифры через точку.")
    @default("192.168.1.1")
    @format("string")
    nasIp: CallMetricIncomingNasIp,
    @description("Кто звонит (Нормализованный телефонный номер)")
    //@validate(Validator.pattern("^[0-9]{10,11}"))
    //@default("79261112233")
    @format("string")
    calling: CallMetricIncomingCalling,
    @description("Куда звонит (Нормализованный телефонный номер)")
    //@validate(Validator.pattern("^[0-9]{10,11}"))
    //@default("79165553344")
    @format("string")
    called: CallMetricIncomingCalled,
    @description("Возможная переадресация (Нормализованный телефонный номер)")
    //@validate(Validator.pattern("^[0-9]{10,11}"))
    //@default("74951233344".some)
    @format("string")
    redirecting: Option[CallMetricIncomingRedirecting],
    @description("Возможный OCPN (Нормализованный телефонный номер)")
    //@validate(Validator.pattern("^[0-9]{10,11}"))
    //@default("74951233344", "74951233344".some)
    //@format("string")
    ocpn: Option[CallMetricIncomingOCPN],
    @description("транк вызова")
    //@default("beeline")
    @format("string")
    inTrunk: CallMetricIncomingInTrunk,
    @description("хедер")
    //@default("12".some)
    @format("string")
    xMCNpbxInfo: Option[CallMetricIncomingXMCNpbxInfo],
    @description("Нормализованный номер А")
    @format("string")
    numberA: Option[CallMetricIncomingNumberA],
    @description("Нормализованный номер B")
    @format("string")
    numberB: Option[CallMetricIncomingNumberB],
    @description("Нормализованный номер C")
    @format("string")
    numberC: Option[CallMetricIncomingNumberC],
    @description("МСН CallID, UUID")
    @format("string")
    mcnCallId: Option[CallMetricIncomingMcnCallId],
    @description("Call_Id (с коммутатора)")
    @format("string")
    callId: CallMetricIncomingCallId,
    @description("Получатели")
    receivers: List[ExtEventReciever],
    @description("Направление")
    direction: Option[CallMetricIncomingDirection] = None
  ) {

    def toMetricOutcoming =
      CallMetricOutcoming(
        state = CallMetricOutcomingState(STATE_START),
        regionId = CallMetricOutcomingRegionId(this.regionId.value),                                                   // Это нужно брать из auth
        nasIp = CallMetricOutcomingNasIp(this.nasIp.value),
        callId = CallMetricOutcomingCallId(this.callId.value),
        calling = CallMetricOutcomingCalling(this.numberA.map(_.value).getOrElse(this.calling.value)),                 // Это нужно брать из auth нормализованным
        called = CallMetricOutcomingCalled(this.numberB.map(_.value).getOrElse(this.called.value)),                    // Это нужно брать из auth нормализованным
        inTrunk = CallMetricOutcomingInTrunk(this.inTrunk.value),
        redirecting =
          this.numberC.map(_.value).orElse(this.redirecting.map(_.value)).map(v => CallMetricOutcomingRedirecting(v)), // Это нужно брать из auth
        setupTime = CallMetricOutcomingSetupTime(this.dt.value).some,
        connectTime = None,
        disconnectTime = None,
        outTrunk = None,
        sessionTime = None,
        disconnectCause = None,
        releasingParty = None,
        ocpn = this.ocpn.map(v => CallMetricOutcomingOCPN(v.value)),
        xMCNpbxInfo = CallMetricOutcomingXMCNpbxInfo(this.xMCNpbxInfo.map(_.value).getOrElse("")).some,
        direction = this.direction.map(v => CallMetricOutcomingDirection(v.value))
      )
  }

  @derive(schema, decoder, encoder)
  @newtype
  case class CallMetricIncomingDt(value: Instant)

  @derive(schema, decoder, encoder)
  @newtype
  case class CallMetricIncomingState(value: Code)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingRegionId(value: Int)

  @derive(schema, decoder, encoder)
  @newtype
  case class CallMetricIncomingNasIp(value: Ipv4Address)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingCalling(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingCalled(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingRedirecting(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingOCPN(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingInTrunk(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingXMCNpbxInfo(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingNumberA(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingNumberB(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingNumberC(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingMcnCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class CallMetricIncomingDirection(value: String)

}
