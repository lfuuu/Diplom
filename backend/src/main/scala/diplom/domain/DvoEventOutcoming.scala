package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.estatico.newtype.macros.newtype

object DvoEventOutcoming {

  @derive(decoder, encoder)
  @newtype
  case class DvoEventOutcomingAction(value: String)

  @derive(decoder, encoder)
  @newtype
  case class DvoEventOutcomingServiceCode(value: String)

  @derive(decoder, encoder)
  @newtype
  case class DvoEventOutcomingCallingPhone(value: String)

  @derive(decoder, encoder)
  @newtype
  case class DvoEventOutcomingRedirectToPhone(value: String)

  case class DvoEventOutcoming(
    action: DvoEventOutcomingAction,
    serviceCode: DvoEventOutcomingServiceCode,
    callingPhone: DvoEventOutcomingCallingPhone,
    redirectToPhone: List[DvoEventOutcomingRedirectToPhone]
  )

  implicit val encoderDvoEventOutcoming: Encoder[DvoEventOutcoming] = new Encoder[DvoEventOutcoming] {

    final def apply(a: DvoEventOutcoming): Json =
      Json
        .obj(
          ("action", a.action.asJson),
          ("service_code", a.serviceCode.asJson),
          ("calling_phone", a.callingPhone.asJson),
          ("redirect_to_phone", a.redirectToPhone.asJson)
        )
        .deepDropNullValues
  }

  implicit val decodeDvoEventOutcoming: Decoder[DvoEventOutcoming] = new Decoder[DvoEventOutcoming] {

    final def apply(c: HCursor): Decoder.Result[DvoEventOutcoming] =
      for {
        action             <- c.downField("action").as[DvoEventOutcomingAction]
        serviceCode        <- c.downField("service_code").as[DvoEventOutcomingServiceCode]
        callingPhone       <- c.downField("calling_phone").as[DvoEventOutcomingCallingPhone]
        redirectToPhonebar <- c.downField("redirect_to_phone").as[List[DvoEventOutcomingRedirectToPhone]]
      } yield new DvoEventOutcoming(action, serviceCode, callingPhone, redirectToPhonebar)
  }

}
