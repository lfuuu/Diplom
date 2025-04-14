package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive

@derive(decoder, encoder)
case class StatusResponse(
  status: String,
  message: String
)
