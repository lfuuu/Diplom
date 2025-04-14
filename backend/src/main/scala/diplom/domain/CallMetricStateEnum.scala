package com.mcn.diplom.domain

import io.circe.{ Decoder, Encoder }
import sttp.tapir._

object CallMetricStateEnum extends Enumeration {
  type Code = Value
  var identifier: Byte = 0
  val STATE_START      = Value(1)
  val STATE_ANSWER     = Value(2)
  val STATE_END        = Value(3)

  implicit val callMetricStateEnumDecoder: Decoder[CallMetricStateEnum.Code] = Decoder.decodeEnumeration(CallMetricStateEnum)
  implicit val CallMetricStateEnumEncoder: Encoder[CallMetricStateEnum.Code] = Encoder.encodeEnumeration(CallMetricStateEnum)

  implicit def CallMetricStateEnumSchema: Schema[CallMetricStateEnum.Code] =
    Schema(SchemaType.SString())
      .validate(Validator.enumeration(CallMetricStateEnum.values.toList, (c: CallMetricStateEnum.Code) => Some(c.toString)))
  //  .default(STATE_START, encoded = Some("STATE_START"))

}
