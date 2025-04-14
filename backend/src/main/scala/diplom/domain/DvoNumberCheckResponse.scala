package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir._

import Schema.annotations._
import ExtEventReciever._

object DvoNumberCheckResponse {

  @derive(decoder, encoder)
  case class DvoNumberCheckResponse(
    @description("номер для проверки.")
    number: DvoNumberCheckResponseNumber,
    receivers: List[ExtEventReciever]
  )

  @derive(decoder, encoder)
  @newtype
  case class DvoNumberCheckResponseNumber(value: String)

  @derive(decoder, encoder)
  @newtype
  case class DvoNumberCheckResponsePushNameNumber(value: String)

}
