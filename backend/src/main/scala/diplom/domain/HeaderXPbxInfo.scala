package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.syntax._
import io.circe.{ Encoder, Json }
import io.estatico.newtype.macros.newtype

object HeaderXPbxInfo {

  case class HeaderXPbxInfo(
    callId: HeaderXPbxInfoCallId,
    cf: HeaderXPbxInfoCF
  )

  @derive(decoder, encoder)
  @newtype
  case class HeaderXPbxInfoCallId(value: String)

  @derive(decoder, encoder)
  @newtype
  case class HeaderXPbxInfoCF(value: String)

  implicit val encoderHeaderXPbxInfo: Encoder[HeaderXPbxInfo] = new Encoder[HeaderXPbxInfo] {

    final def apply(a: HeaderXPbxInfo): Json =
      Json
        .obj(
          ("call_id", a.callId.asJson),
          ("CF", a.cf.asJson)
        )
        .deepDropNullValues
  }

}
