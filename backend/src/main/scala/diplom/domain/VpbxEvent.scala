package com.mcn.diplom.domain

import java.util.UUID

import com.mcn.diplom.domain.DvoEventOutcoming._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype
import sttp.tapir.Schema
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema

import Schema.annotations._

object VpbxEvent {

  import com.mcn.diplom.domain.VpbxEventDataForwardType._

  @derive(schema, decoder, encoder)
  case class VpbxEvent(
    id: VpbxEventId,
    data: VpbxEventData,
    `type`: VpbxEventType
  )

  @derive(schema, decoder, encoder)
  case class VpbxEventData(
    //@default(VpbxEventDataDid("74951097495").some)   /// никак не могу подобрать правильную работу с Option
    //@format("74951097495")
    did: Option[VpbxEventDataDid],
    userId: Option[VpbxEventDataUserId],
    enabled: Option[VpbxEventDataEnabled],
    eventTs: Option[VpbxEventDataEventTs],
    regionId: Option[VpbxEventDataRegionId],
    accountId: Option[VpbxEventDataAccountId],
    @default("update_forward")
    @format("string")
    eventName: VpbxEventDataEventName,
    forwardType: Option[VpbxEventDataForwardType],
    eventVersion: Option[VpbxEventDataEventVersion],
    statProductId: Option[VpbxEventDataStatProductId],
    supportUserId: Option[VpbxEventDataSupportUserId],
    smartFeatureId: Option[VpbxEventDataSmartFeatureId],
    didForwardTarget: Option[List[VpbxEventDataDidForwardTarget]]
  ) {

    def toDvoEventOutcoming =
      for {
        action          <- enabled.map(_.toDvoEventOutcomingAction)
        serviceCode     <- forwardType.map(_.toDvoEventOutcomingServiceCode)
        callingPhone    <- did.map(_.toDvoEventOutcomingCallingPhone)
        redirectToPhone <- didForwardTarget.map(_.map(_.toDvoEventOutcomingRedirectToPhone))
      } yield DvoEventOutcoming(action, serviceCode, callingPhone, redirectToPhone)
  }

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventId(value: UUID)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventType(value: String)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataDid(value: String) {
    def toDvoEventOutcomingCallingPhone = DvoEventOutcomingCallingPhone(value)
  }

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataUserId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataEnabled(value: Boolean) {
    def toDvoEventOutcomingAction = DvoEventOutcomingAction(if (value) "on" else "off")
  }

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataEventTs(value: Long)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataRegionId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataAccountId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataEventName(value: String)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataEventVersion(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataStatProductId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataSupportUserId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataSmartFeatureId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class VpbxEventDataDidForwardTarget(value: String) {
    def toDvoEventOutcomingRedirectToPhone = DvoEventOutcomingRedirectToPhone(value)
  }

}
