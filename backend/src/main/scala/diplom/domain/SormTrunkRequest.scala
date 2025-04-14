package com.mcn.diplom.domain

import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.generic.auto._
import io.estatico.newtype.macros.newtype

@derive(decoder, encoder)
case class SormTrunkRequest(
  regionId: SormTrunkRequest.SormTrunkRequestRegionId,
  usType: SormTrunkRequest.SormTrunkRequestUsType,
  trunkOrmId: Option[SormTrunkRequest.SormTrunkRequestTrunkOrmId]
)

object SormTrunkRequest {

  @derive(decoder, encoder)
  @newtype
  case class SormTrunkRequestRegionId(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class SormTrunkRequestUsType(value: Int)

  @derive(decoder, encoder)
  @newtype
  case class SormTrunkRequestTrunkOrmId(value: Int)

}
