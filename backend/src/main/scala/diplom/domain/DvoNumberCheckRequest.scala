package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir._

import Schema.annotations._

object DvoNumberCheckRequest {

  @derive(decoder, encoder)
  case class DvoNumberCheckRequest(
    @description("номер для проверки.")
    number: DvoNumberCheckRequestNumber
  )

  @derive(decoder, encoder)
  @newtype
  case class DvoNumberCheckRequestNumber(value: String)
}
