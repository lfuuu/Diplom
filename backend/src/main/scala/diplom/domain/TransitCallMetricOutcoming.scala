package com.mcn.diplom.domain

import cats.syntax.all._
import com.mcn.diplom.domain.misc._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype

object TransitCallMetricOutcoming {

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingNumA(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingNumB(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingNumC(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingNumD(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingDate(value: Timestamp)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingDateAcc(value: Timestamp)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingIdSrc(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingIdDst(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingSessionId(value: Long)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingIdRel(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingVrfRsp(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingResponse(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingReleaseCode(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingCallDuration(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingIdUvrT(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingCallType(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingIsTransit(value: Boolean)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingEntryServerId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingCurrentTrunkName(value: String)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingServerId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class TransitCallMetricOutcomingTrunkName(value: String)

  @derive(decoder, encoder)
  case class TransitCallMetricOutcoming(
    numA: TransitCallMetricOutcomingNumA,
    numB: TransitCallMetricOutcomingNumB,
    numC: Option[TransitCallMetricOutcomingNumC] = None,
    numD: Option[TransitCallMetricOutcomingNumD] = None,
    date: TransitCallMetricOutcomingDate,
    dateAcc: TransitCallMetricOutcomingDateAcc,
    idSrc: Option[TransitCallMetricOutcomingIdSrc] = None,
    idDst: Option[TransitCallMetricOutcomingIdDst] = None,
    sessionId: Option[TransitCallMetricOutcomingSessionId] = None,
    idRel: Option[TransitCallMetricOutcomingIdRel] = None,
    vrfRsp: Option[TransitCallMetricOutcomingVrfRsp] = None,
    response: Option[TransitCallMetricOutcomingResponse] = None,
    releaseCode: Option[TransitCallMetricOutcomingReleaseCode] = None,
    callDuration: Option[TransitCallMetricOutcomingCallDuration] = None,
    idUvrT: Option[TransitCallMetricOutcomingIdUvrT] = None,
    callId: TransitCallMetricOutcomingCallId,
    callType: Option[TransitCallMetricOutcomingCallType] = TransitCallMetricOutcomingCallType(1).some,
    isTransit: Option[TransitCallMetricOutcomingIsTransit] = TransitCallMetricOutcomingIsTransit(true).some,
    currentTrunkName: Option[TransitCallMetricOutcomingCurrentTrunkName] = None,
    entryServerId: Option[TransitCallMetricOutcomingEntryServerId] = None,
    trunkName: Option[TransitCallMetricOutcomingTrunkName] = None,
    serverId: Option[TransitCallMetricOutcomingServerId] = None
  )
}
