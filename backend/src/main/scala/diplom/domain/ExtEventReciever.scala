package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema

object ExtEventReciever {

  @derive(schema, decoder, encoder)
  case class ExtEventReciever(
    name: ExtEventRecieverName,
    urls: List[ExtEventRecieverUrl]
  )

  @derive(decoder, encoder)
  @newtype
  case class ExtEventRecieverName(value: String)

  @derive(decoder, encoder)
  @newtype
  case class ExtEventRecieverDirection(value: String)

  @derive(decoder, encoder)
  @newtype
  case class ExtEventRecieverUrl(value: String)

}
