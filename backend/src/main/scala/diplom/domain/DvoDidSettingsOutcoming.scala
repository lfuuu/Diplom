package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.estatico.newtype.macros.newtype

object DvoDidSettingsOutcoming {

  @derive(decoder, encoder)
  @newtype
  case class DvoDidSettingsOutcomingServiceCode(value: String)

  @derive(decoder, encoder)
  @newtype
  case class DvoDidSettingsOutcomingCallingPhone(value: String)

  case class DvoDidSettingsOutcoming(
    callingPhone: DvoDidSettingsOutcomingServiceCode,
    serviceCode: List[DvoDidSettingsOutcomingServiceCode]
  )

  implicit val encoderDvoDidSettingsOutcoming: Encoder[DvoDidSettingsOutcoming] = new Encoder[DvoDidSettingsOutcoming] {

    final def apply(a: DvoDidSettingsOutcoming): Json =
      Json
        .obj(
          ("calling_phone", a.callingPhone.asJson),
          ("service_code", a.serviceCode.asJson)
        )
        .deepDropNullValues
  }

  implicit val decodeDvoDidSettingsOutcoming: Decoder[DvoDidSettingsOutcoming] = new Decoder[DvoDidSettingsOutcoming] {

    final def apply(c: HCursor): Decoder.Result[DvoDidSettingsOutcoming] =
      for {
        callingPhone <- c.downField("calling_phone").as[DvoDidSettingsOutcomingServiceCode]
        serviceCode  <- c.downField("service_code").as[List[DvoDidSettingsOutcomingServiceCode]]
      } yield new DvoDidSettingsOutcoming(callingPhone, serviceCode)
  }

}
