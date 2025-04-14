package com.mcn.diplom.domain

import java.time.Instant
import java.util.UUID

import scala.util.Try

import cats.syntax.all._
import com.comcast.ip4s.Ipv4Address
import com.mcn.diplom.domain.CallMetricStateEnum._
import com.mcn.diplom.domain.CdrAccountStatusTypeEnum.{ START => START }
import com.mcn.diplom.domain.TransitCallMetricIncoming._
import com.mcn.diplom.domain.TransitCallMetricOutcoming._
import com.mcn.diplom.domain.misc.Timestamp
import com.mcn.diplom.services.radiusAccounting.RadiusAttribute.{ RadiusAttributeName, RadiusAttributeVendorCode, RadiusAttributeVendorId }
import com.mcn.diplom.services.radiusAccounting.{ RadiusDictionary, RadiusPacket }
import io.estatico.newtype.macros.newtype

import CallMetricIncoming._
import CallMetricOutcoming._
import Cdr._

sealed trait CdrSessionTimeRoundingType
object CEIL  extends CdrSessionTimeRoundingType
object ROUND extends CdrSessionTimeRoundingType
object FLOOR extends CdrSessionTimeRoundingType

object CdrAccountStatusTypeEnum extends Enumeration {
  var identifier: Byte = 1
  val START            = Value(1)
  val STOP             = Value(2)
}

case class Cdr(
  accountStatusType: CdrAccountStatusType,
  callId: CdrCallId,
  nasIp: CdrNasIp,
  srcNumber: Option[CdrSrcNumber],
  dstNumber: Option[CdrDstNumber],
  redirectNumber: Option[CdrRedirectNumber],
  sessionTimePrecise: Option[CdrSessionTimePrecise],
  sessionTime: Option[CdrSessionTime],
  setupTime: Option[CdrSetupTime],
  connectTime: Option[CdrConnectTime],
  disconnectTime: Option[CdrDisconnectTime],
  disconnectCause: Option[CdrDisconnectCause],
  srcRoute: Option[CdrSrcRoute],
  dstRoute: Option[CdrDstRoute],
  srcNoa: Option[CdrSrcNoa],
  dstNoa: Option[CdrDstNoa],
  dstReplace: Option[CdrDstReplace],
  callFinished: Option[CdrCallFinished],
  releasingParty: Option[CdrReleasingParty],
  inSigCallId: Option[CdrInSigCallId],
  outSigCallId: Option[CdrOutSigCallId],
  mcnCallId: Option[CdrMCNCallId],
  redirectNoa: Option[CdrRedirectNoa],
  outgoingRedirectNumber: Option[CdrOutgoingRedirectNumber],
  outgoingRedirectNumberNoa: Option[CdrOutgoingRedirectNumberNoa],
  ocpnNumber: Option[CdrOcpnNumber],
  ocpnNumberNoa: Option[CdrOcpnNumberNoa],
  cpc: Option[CdrCpc]
) {

  def toTransitMetricOutcoming(auth: TransitCallMetricIncoming) = {

    val idDstEPVV = this.dstRoute.flatMap(route => auth.dstTrunkItem.filter(t => t.trunkName.value == route.value).headOption)

    TransitCallMetricOutcoming(
      numA = TransitCallMetricOutcomingNumA(auth.numA.value),
      numB = TransitCallMetricOutcomingNumB(auth.numB.value),
      numC = auth.numC.map(i => TransitCallMetricOutcomingNumC(i.value)),
      numD = auth.ocpn.map(i => TransitCallMetricOutcomingNumD(i.value)),
      date = TransitCallMetricOutcomingDate(Timestamp(Instant.ofEpochSecond(auth.radiusResponseTime.value))),
      dateAcc = TransitCallMetricOutcomingDateAcc(Timestamp(Instant.ofEpochSecond(auth.radiusResponseTime.value))),
      callId = TransitCallMetricOutcomingCallId(auth.mcnCallId.map(_.value).getOrElse("")),
      idSrc = TransitCallMetricOutcomingIdSrc(auth.idSrcEPVV.value.toIntOption.getOrElse(0)).some,
      //idDst = TransitCallMetricOutcomingIdDst(idDstEPVV.value.toIntOption.getOrElse(0)).some
      idDst = idDstEPVV.map(i => TransitCallMetricOutcomingIdDst(i.idDstEPVV.value.toIntOption.getOrElse(-1))),
      currentTrunkName = auth.currentTrunkName.map(i => TransitCallMetricOutcomingCurrentTrunkName(i.value)),
      entryServerId = auth.entryServerId.map(i => TransitCallMetricOutcomingEntryServerId(i.value)),
      trunkName = auth.currentTrunkName.map(i => TransitCallMetricOutcomingTrunkName(i.value)),
      serverId = TransitCallMetricOutcomingServerId(auth.serverId.value).some
    )
  }

  def toMetricOutcoming(auth: CallMetricIncoming) =
    CallMetricOutcoming(
      state = CallMetricOutcomingState(this.accountStatusType.value match {
        case START => STATE_ANSWER
        case _     => STATE_END
      }),
      regionId = CallMetricOutcomingRegionId(auth.regionId.value), // Это нужно брать из auth
      callId = CallMetricOutcomingCallId(this.callId.value.toString),
      nasIp = CallMetricOutcomingNasIp(this.nasIp.value),
      calling = CallMetricOutcomingCalling(auth.numberA.map(_.value).getOrElse("")), // Это нужно брать из auth нормализованным
      called = CallMetricOutcomingCalled(auth.numberB.map(_.value).getOrElse("")),   // Это нужно брать из auth нормализованным
      // calling = CallMetricOutcomingCalling(this.srcNumber.map(_.value).getOrElse("")),     // Это нужно брать из auth нормализованным
      // called = CallMetricOutcomingCalled(this.dstNumber.map(_.value).getOrElse("")),       // Это нужно брать из auth нормализованным
      inTrunk = CallMetricOutcomingInTrunk(this.srcRoute.map(_.value).getOrElse("")),
      redirecting = auth.numberC.map(v => CallMetricOutcomingRedirecting(v.value)),  // Это нужно брать из auth
      // redirecting = this.redirectNumber.map(v => CallMetricOutcomingRedirecting(v.value)), // Это нужно брать из auth
      setupTime = this.setupTime.map(v => CallMetricOutcomingSetupTime(parseToInstant(v.value))),
      connectTime = this.setupTime.map(v => CallMetricOutcomingConnectTime(parseToInstant(v.value))),
      disconnectTime = this.disconnectTime.map(v => CallMetricOutcomingDisconnectTime(parseToInstant(v.value))),
      outTrunk = this.dstRoute.map(v => CallMetricOutcomingOutTrunk(v.value)),
      sessionTime = this.sessionTime.map(v => CallMetricOutcomingSessionTime(v.value)),
      disconnectCause = this.disconnectCause.map(v => CallMetricOutcomingDisconnectCause(v.value.toString)),
      releasingParty = this.releasingParty.map(v => CallMetricOutcomingReleasingParty(v.value)),
      ocpn = auth.ocpn.map(v => CallMetricOutcomingOCPN(v.value)),
      xMCNpbxInfo = auth.xMCNpbxInfo.map(v => CallMetricOutcomingXMCNpbxInfo(v.value)),
      direction = auth.direction.map(v => CallMetricOutcomingDirection(v.value))
    )

  def toPrettyLog = {
    val accountStatusType  = this.accountStatusType.value
    val src_number         = this.srcNumber.map(s => s", src_number=${s.value}").getOrElse("")
    val dst_number         = this.dstNumber.map(s => s", dst_number=${s.value}").getOrElse("")
    val redirectNumber     = this.redirectNumber.map(s => s", redirect_number=${s.value}").getOrElse("")
    val sessionTimePrecise = this.sessionTimePrecise.map(s => s", session_time_precise=${s.value}").getOrElse("")
    val sessionTime        = this.sessionTime.map(s => s", session_time=${s.value}").getOrElse("")

    val setupTime      = this.setupTime.map(s => s", setup_time=${s.value}").getOrElse("")
    val connectTime    = this.connectTime.map(s => s", connect_time=${s.value}").getOrElse("")
    val disconnectTime = this.disconnectTime.map(s => s", disconnect_time=${s.value}").getOrElse("")

    val disconnectCause = this.disconnectCause.map(s => s", disconnect_cause=${s.value}").getOrElse("")
    val srcRoute        = this.srcRoute.map(s => s", src_route=${s.value}").getOrElse("")
    val dstRoute        = this.dstRoute.map(s => s", dst_route=${s.value}").getOrElse("")

    val srcNoa = this.srcNoa.map(s => s", src_noa=${s.value}").getOrElse("")
    val dstNoa = this.dstNoa.map(s => s", dst_noa=${s.value}").getOrElse("")

    val dstReplace     = this.dstReplace.map(s => s", dst_replace=${s.value}").getOrElse("")
    val callFinished   = this.callFinished.map(s => s", call_finished=${s.value}").getOrElse("")
    val releasingParty = this.releasingParty.map(s => s", releasing_party=${s.value}").getOrElse("")

    val inSigCallId  = this.inSigCallId.map(s => s", in_sig_call_id=${s.value}").getOrElse("")
    val outSigCallId = this.outSigCallId.map(s => s", out_sig_call_id=${s.value}").getOrElse("")

    val mcnCallId   = this.mcnCallId.map(s => s", mcn_call_id=${s.value}").getOrElse("")
    val redirectNoa = this.redirectNoa.map(s => s", redirect_noa=${s.value}").getOrElse("")

    val outgoingRedirectNumber    = this.outgoingRedirectNumber.map(s => s", outgoing_redirect_number=${s.value}").getOrElse("")
    val outgoingRedirectNumberNoa = this.outgoingRedirectNumberNoa.map(s => s", outgoing_redirect_number_noa=${s.value}").getOrElse("")

    val ocpnNumber    = this.ocpnNumber.map(s => s", ocpn_number=${s.value}").getOrElse("")
    val ocpnNumberNoa = this.ocpnNumberNoa.map(s => s", ocpn_number_noa=${s.value}").getOrElse("")

    val cpc = this.cpc.map(s => s", cpc=${s.value}").getOrElse("")

    s"$accountStatusType call_id=${callId.value}, nas_ip=${nasIp.value}" +
      s"$srcRoute$dstRoute" +
      s"$src_number$srcNoa$dst_number$dstNoa$dstReplace$redirectNoa$outgoingRedirectNumber$outgoingRedirectNumberNoa$ocpnNumber$ocpnNumberNoa" +
      s"$redirectNumber$sessionTimePrecise$sessionTime$setupTime$connectTime$disconnectTime$disconnectCause$callFinished" +
      s"$inSigCallId$outSigCallId$mcnCallId$cpc$releasingParty"
  }

}

object Cdr {

  @newtype
  case class CdrAccountStatusType(value: CdrAccountStatusTypeEnum.Value)

  @newtype
  case class CdrCallId(value: Long)

  @newtype
  case class CdrNasIp(value: Ipv4Address)

  @newtype
  case class CdrSrcNumber(value: String)

  @newtype
  case class CdrDstNumber(value: String)

  @newtype
  case class CdrRedirectNumber(value: String)

  @newtype
  case class CdrSessionTimePrecise(value: Double)

  @newtype
  case class CdrSessionTime(value: Long)

  @newtype
  case class CdrSetupTime(value: String)

  @newtype
  case class CdrConnectTime(value: String)

  @newtype
  case class CdrDisconnectTime(value: String)

  @newtype
  case class CdrDisconnectCause(value: Int)

  @newtype
  case class CdrSrcRoute(value: String)

  @newtype
  case class CdrDstRoute(value: String)

  @newtype
  case class CdrSrcNoa(value: Int)

  @newtype
  case class CdrDstNoa(value: Int)

  @newtype
  case class CdrDstReplace(value: String)

  @newtype
  case class CdrCallFinished(value: String)

  @newtype
  case class CdrReleasingParty(value: String)

  @newtype
  case class CdrInSigCallId(value: String)

  @newtype
  case class CdrOutSigCallId(value: String)

  @newtype
  case class CdrMCNCallId(value: String = "00000000-0000-0000-0000-000000000000")

  @newtype
  case class CdrRedirectNoa(value: Int)

  @newtype
  case class CdrOutgoingRedirectNumber(value: String)

  @newtype
  case class CdrOutgoingRedirectNumberNoa(value: Int)

  @newtype
  case class CdrOcpnNumber(value: String)

  @newtype
  case class CdrOcpnNumberNoa(value: Int)

  @newtype
  case class CdrCpc(value: String)

  def fromRadiusPacket(packet: RadiusPacket, roundingType: CdrSessionTimeRoundingType = CEIL)(implicit dic: RadiusDictionary) = {
    implicit val vendorId = RadiusAttributeVendorId(9)

    val session_time_precise =
      dic.getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "CALLDURATIONUS")(packet).flatMap(i => Try(i.toDouble).toOption)
    val session_time         = session_time_precise.map(i =>
      roundingType match {
        case CEIL  => math.ceil(i).toLong
        case FLOOR => math.floor(i).toLong
        case ROUND => i.toLong
      }
    )

    for {
      accountStatusType <- dic
                             .getIntFromStandartAttributeByName(RadiusAttributeName("Acct-Status-Type"))(packet)
                             .map(i => CdrAccountStatusType(CdrAccountStatusTypeEnum(i)))
      callId            <- dic
                  .getStringFromStandartAttributeByName(RadiusAttributeName("Acct-Session-Id"))(packet)
                  .flatMap(i => Try(i.toLong).toOption)
                  .map(CdrCallId(_))
      naspIp            <- dic
                  .getIPV4FromStandartAttributeByName(RadiusAttributeName("NAS-IP-Address"))(packet)
                  .map(CdrNasIp(_)),

    } yield Cdr(
      accountStatusType = accountStatusType,
      callId = callId,
      nasIp = naspIp,
      srcNumber = dic
        .getStringFromStandartAttributeByName(RadiusAttributeName("Calling-Station-Id"))(packet)
        .map(CdrSrcNumber(_))
        .orElse(CdrSrcNumber("").some),
      dstNumber = dic
        .getStringFromStandartAttributeByName(RadiusAttributeName("Called-Station-Id"))(packet)
        .map(CdrDstNumber(_))
        .orElse(CdrDstNumber("").some),
      redirectNumber = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "INCOMING-REDIRECTING-ADDRESS")(packet)
        .map(CdrRedirectNumber(_))
        .orElse(CdrRedirectNumber("").some),
      sessionTimePrecise = session_time_precise
        .map(CdrSessionTimePrecise(_))
        .orElse(CdrSessionTimePrecise(0f).some),
      sessionTime = session_time
        .map(CdrSessionTime(_))
        .orElse(CdrSessionTime(0).some),
      setupTime = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(25))(packet)
        .map(CdrSetupTime(_)),
      connectTime = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(28))(packet)
        .map(CdrConnectTime(_)),
      disconnectTime = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(29))(packet)
        .map(CdrDisconnectTime(_)),
      disconnectCause = dic
        .getIntFromVendorAvpValue(RadiusAttributeVendorCode(30))(packet)
        .map(CdrDisconnectCause(_)),
      srcRoute = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "INCOMING-ROUTEID")(packet)
        .map(CdrSrcRoute(_))
        .orElse(CdrSrcRoute("").some),
      dstRoute = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "OUTGOING-ROUTEID")(packet)
        .map(CdrDstRoute(_))
        .orElse(CdrDstRoute("").some),
      srcNoa = dic
        .getIntFromVendorAvpValue(RadiusAttributeVendorCode(1), "INCOMING-CALLING-ADDRESS-NOA")(packet)
        .map(CdrSrcNoa(_))
        .orElse(CdrSrcNoa(0).some),
      dstNoa = dic
        .getIntFromVendorAvpValue(RadiusAttributeVendorCode(1), "INCOMING-CALLED-ADDRESS-NOA")(packet)
        .map(CdrDstNoa(_))
        .orElse(CdrDstNoa(0).some),
      dstReplace = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "OUTGOING-REPLACE-CDPN")(packet)
        .map(CdrDstReplace(_)),
      callFinished = None,
      releasingParty = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "CALL-TERMINATING-PARTY")(packet)
        .map(CdrReleasingParty(_)),
      inSigCallId = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "H323-INCOMING-CONF-ID")(packet)
        .map(CdrInSigCallId(_)),
      outSigCallId = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "OUTGOINGSIGNALLINGCALLID")(packet)
        .map(CdrOutSigCallId(_)),
      mcnCallId = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "X-MCN-CALLID")(packet)
        .flatMap(i => Try(UUID.fromString(i)).toOption.map(_.toString))
        .map(i => CdrMCNCallId(i)), // Обход странной ошибки с задвоением. нужно брать левые 36 символов
      redirectNoa = dic
        .getIntFromVendorAvpValue(RadiusAttributeVendorCode(1), "INCOMING-REDIRECTING-ADDRESS-NOA")(packet)
        .map(CdrRedirectNoa(_))
        .orElse(CdrRedirectNoa(0).some),
      outgoingRedirectNumber = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "OUTGOING-REDIRECTING-ADDRESS")(packet)
        .map(CdrOutgoingRedirectNumber(_))
        .orElse(CdrOutgoingRedirectNumber("").some),
      outgoingRedirectNumberNoa = dic
        .getIntFromVendorAvpValue(RadiusAttributeVendorCode(1), "OUTGOING-REDIRECTING-ADDRESS-NOA")(packet)
        .map(CdrOutgoingRedirectNumberNoa(_))
        .orElse(CdrOutgoingRedirectNumberNoa(0).some),
      ocpnNumber = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "OCPN")(packet)
        .map(CdrOcpnNumber(_))
        .orElse(CdrOcpnNumber("").some),
      ocpnNumberNoa = dic
        .getIntFromVendorAvpValue(RadiusAttributeVendorCode(1), "OCPN-NOA")(packet)
        .map(CdrOcpnNumberNoa(_))
        .orElse(CdrOcpnNumberNoa(0).some),
      cpc = dic
        .getStringFromVendorAvpValue(RadiusAttributeVendorCode(1), "CALLINGPARTYCATEGORY")(packet)
        .map(CdrCpc(_))
    )
  }

}
