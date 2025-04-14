package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._

@derive(decoder, encoder)
case class SormTrunk(
  trunkOrmId: SormTrunkOrmId,
  trunkName: SormTrunkName
)

@derive(decoder, encoder)
case class SormTrunkOrmId(value: Int) extends AnyVal

@derive(decoder, encoder)
case class SormTrunkName(value: String) extends AnyVal

object SormTrunk {}
