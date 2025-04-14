package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import sttp.tapir._
import sttp.tapir.derevo.schema

import Schema.annotations._

@derive(schema, decoder, encoder)
@description("Заткнись, пожайлуста")
case class ShutUpIncoming()

object ShutUpIncoming {}
