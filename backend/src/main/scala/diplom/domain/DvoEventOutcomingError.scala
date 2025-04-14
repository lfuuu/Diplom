package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._

@derive(decoder, encoder)
case class DvoEventOutcomingError(
  status: DvoEventOutcomingErrorStatus,
  message: DvoEventOutcomingErrorMessage
)

@derive(decoder, encoder)
case class DvoEventOutcomingErrorStatus(value: String) extends AnyVal

@derive(decoder, encoder)
case class DvoEventOutcomingErrorMessage(value: String) extends AnyVal

object DvoEventOutcomingError {}
