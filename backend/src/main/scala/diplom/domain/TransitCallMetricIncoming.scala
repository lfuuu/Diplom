package com.mcn.diplom.domain

import com.comcast.ip4s.Ipv4Address
import com.mcn.diplom.domain.misc._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype
import sttp.tapir.Schema
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema

import Schema.annotations._

@derive(schema, decoder, encoder)
case class TransitCallMetricKey(
  nasIp: TransitCallMetricIncoming.TransitCallMetricIncomingNaspIp,
  callId: TransitCallMetricIncoming.TransitCallMetricIncomingCallId
)

object TransitCallMetricKey {
  def fromTransitCallMetricIncoming(metric: TransitCallMetricIncoming.TransitCallMetricIncoming) = TransitCallMetricKey(metric.nasIp, metric.callId)

  def fromCdr(cdr: Cdr) =
    TransitCallMetricKey(
      TransitCallMetricIncoming.TransitCallMetricIncomingNaspIp(cdr.nasIp.value),
      TransitCallMetricIncoming.TransitCallMetricIncomingCallId(cdr.callId.value.toString)
    )
}

object TransitCallMetricIncoming {

  @derive(schema, decoder, encoder)
  @description("событие метрики транзитного звонка")
  case class TransitCallMetricIncoming(
    @description("идетнификатор звонка с коммутатора")
    callId: TransitCallMetricIncomingCallId,
    @description("MCN CallId")
    mcnCallId: Option[TransitCallMetricIncomingMcnCallId],
    @description("ip-адрес коммутатора, четыре цифры через точку.")
    @default("192.168.1.1")
    @format("string")
    nasIp: TransitCallMetricIncomingNaspIp,
    @description("Кто звонит (Нормализованный телефонный номер)")
    numA: TransitCallMetricIncomingNumberA,
    @description("Куда звонит (Нормализованный телефонный номер)")
    numB: TransitCallMetricIncomingNumberB,
    @description("Возможная переадресация (Нормализованный телефонный номер)")
    numC: Option[TransitCallMetricIncomingNumberC],
    @description("Возможный OCPN (Нормализованный телефонный номер)")
    ocpn: Option[TransitCallMetricIncomingOCPN],
    @description("Время события")
    radiusResponseTime: TransitCallMetricIncomingRadiusResponseTime,
    @description("id транзитного оператора из оригинационного плеча вызова")
    idSrcEPVV: TransitCallMetricIncomingIdSrcEPVV,
    @description("название оригинационного транка")
    trunkName: TransitCallMetricIncomingTrunkName,
    @description("регион оригинационного транка")
    serverId: TransitCallMetricIncomingServerId,
    @description("Список возможных терминационных транков")
    dstTrunkItem: List[TransitCallMetricIncomingDstTrunkItem],
    @description("Транк на текущем узле")
    currentTrunkName: Option[TransitCallMetricIncomingCurrentTrunkName],
    @description("Входящий регион")
    entryServerId: Option[TransitCallMetricIncomingEntryServerId]
  )

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingMcnCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingNaspIp(value: Ipv4Address)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingNumberA(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingNumberB(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingNumberC(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingOCPN(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingRadiusResponseTime(value: Long)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingIdSrcEPVV(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingServerId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingTrunkName(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingDstTrunkItemTrunkName(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingDstTrunkItemIdDstEPVV(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingEntrServerId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingEntryServerId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricIncomingCurrentTrunkName(value: String)

  @derive(schema, decoder, encoder)
  case class TransitCallMetricIncomingDstTrunkItem(
    trunkName: TransitCallMetricIncomingDstTrunkItemTrunkName,
    idDstEPVV: TransitCallMetricIncomingDstTrunkItemIdDstEPVV
  )

}
