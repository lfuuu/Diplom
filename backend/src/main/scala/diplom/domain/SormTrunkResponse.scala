package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._

@derive(decoder, encoder)
case class SormTrunkResponse(
  trunks: List[SormTrunk]
)

object SormTrunkResponse {}
