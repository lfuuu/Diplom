package com.mcn.diplom.sql

import com.mcn.diplom.domain.Cdr._
import com.mcn.diplom.domain.CdrAccountStatusTypeEnum
import com.mcn.diplom.sql.codecs.inet
import skunk._
import skunk.codec.all._

object codecsTele2Abonent {

  val cdrAccountStatusType: Codec[CdrAccountStatusType] =
    int4.imap(i => CdrAccountStatusType(CdrAccountStatusTypeEnum(i)))(_.value.id)

  val callId: Codec[CdrCallId] = int8.imap(CdrCallId(_))(_.value)

  val cdrNasIp: Codec[CdrNasIp] =
    inet.imap[CdrNasIp](CdrNasIp(_))(_.value)

  val cdrSrcNumberO: Codec[Option[CdrSrcNumber]] =
    varchar.opt.imap[Option[CdrSrcNumber]](_.map(CdrSrcNumber(_)))(_.map(_.value))

  val cdrDstNumberO: Codec[Option[CdrDstNumber]] =
    varchar.opt.imap[Option[CdrDstNumber]](_.map(CdrDstNumber(_)))(_.map(_.value))

  val cdrRedirectNumberO: Codec[Option[CdrRedirectNumber]] =
    varchar.opt.imap[Option[CdrRedirectNumber]](_.map(CdrRedirectNumber(_)))(_.map(_.value))

  val cdrSessionTimePreciseO: Codec[Option[CdrSessionTimePrecise]] =
    float8.opt.imap[Option[CdrSessionTimePrecise]](_.map(CdrSessionTimePrecise(_)))(_.map(_.value))

  val cdrSessionTimeO: Codec[Option[CdrSessionTime]] =
    int8.opt.imap[Option[CdrSessionTime]](_.map(CdrSessionTime(_)))(_.map(_.value))

  val cdrSetupTimeO: Codec[Option[CdrSetupTime]] =
    varchar.opt.imap[Option[CdrSetupTime]](_.map(CdrSetupTime(_)))(_.map(_.value))

  val cdrConnectTimeO: Codec[Option[CdrConnectTime]] =
    varchar.opt.imap[Option[CdrConnectTime]](_.map(CdrConnectTime(_)))(_.map(_.value))

  val CdrDisconnectTimeO: Codec[Option[CdrDisconnectTime]] =
    varchar.opt.imap[Option[CdrDisconnectTime]](_.map(CdrDisconnectTime(_)))(_.map(_.value))

  val cdrDisconnectCauseO: Codec[Option[CdrDisconnectCause]] =
    int2.opt.imap[Option[CdrDisconnectCause]](_.map(i => CdrDisconnectCause(i.toInt)))(_.map(_.value.toShort))

  val cdrSrcRouteO: Codec[Option[CdrSrcRoute]] =
    varchar.opt.imap[Option[CdrSrcRoute]](_.map(CdrSrcRoute(_)))(_.map(_.value))

  val cdrDstRouteO: Codec[Option[CdrDstRoute]] =
    varchar.opt.imap[Option[CdrDstRoute]](_.map(CdrDstRoute(_)))(_.map(_.value))

  val cdrSrcNoaO: Codec[Option[CdrSrcNoa]] =
    int2.opt.imap[Option[CdrSrcNoa]](_.map(i => CdrSrcNoa(i.toInt)))(_.map(_.value.toShort))

  val cdrDstNoaO: Codec[Option[CdrDstNoa]] =
    int2.opt.imap[Option[CdrDstNoa]](_.map(i => CdrDstNoa(i.toInt)))(_.map(_.value.toShort))

  val cdrDstReplaceO: Codec[Option[CdrDstReplace]] =
    varchar.opt.imap[Option[CdrDstReplace]](_.map(CdrDstReplace(_)))(_.map(_.value))

  val cdrCallFinishedO: Codec[Option[CdrCallFinished]] =
    varchar.opt.imap[Option[CdrCallFinished]](_.map(CdrCallFinished(_)))(_.map(_.value))

  val cdrReleasingPartyO: Codec[Option[CdrReleasingParty]] =
    varchar.opt.imap[Option[CdrReleasingParty]](_.map(CdrReleasingParty(_)))(_.map(_.value))

  val cdrInSigCallIdO: Codec[Option[CdrInSigCallId]] =
    varchar.opt.imap[Option[CdrInSigCallId]](_.map(CdrInSigCallId(_)))(_.map(_.value))

  val cdrOutSigCallIdO: Codec[Option[CdrOutSigCallId]] =
    varchar.opt.imap[Option[CdrOutSigCallId]](_.map(CdrOutSigCallId(_)))(_.map(_.value))

  val cdrMCNCallIdO: Codec[Option[CdrMCNCallId]] =
    varchar.opt.imap[Option[CdrMCNCallId]](_.map(CdrMCNCallId(_)))(_.map(_.value))

  val cdrRedirectNoaO: Codec[Option[CdrRedirectNoa]] =
    int2.opt.imap[Option[CdrRedirectNoa]](_.map(i => CdrRedirectNoa(i.toInt)))(_.map(_.value.toShort))

  val cdrOutgoingRedirectNumberO: Codec[Option[CdrOutgoingRedirectNumber]] =
    varchar.opt.imap[Option[CdrOutgoingRedirectNumber]](_.map(CdrOutgoingRedirectNumber(_)))(_.map(_.value))

  val cdrOutgoingRedirectNumberNoaO: Codec[Option[CdrOutgoingRedirectNumberNoa]] =
    int2.opt.imap[Option[CdrOutgoingRedirectNumberNoa]](_.map(i => CdrOutgoingRedirectNumberNoa(i.toInt)))(_.map(_.value.toShort))

  val cdrOcpnNumberO: Codec[Option[CdrOcpnNumber]] =
    varchar.opt.imap[Option[CdrOcpnNumber]](_.map(CdrOcpnNumber(_)))(_.map(_.value))

  val cdrOcpnNumberNoaO: Codec[Option[CdrOcpnNumberNoa]] =
    int2.opt.imap[Option[CdrOcpnNumberNoa]](_.map(i => CdrOcpnNumberNoa(i.toInt)))(_.map(_.value.toShort))

  val cdrCpcO: Codec[Option[CdrCpc]] =
    varchar.opt.imap[Option[CdrCpc]](_.map(CdrCpc(_)))(_.map(_.value))

}
