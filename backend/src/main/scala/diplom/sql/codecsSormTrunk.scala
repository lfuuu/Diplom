package com.mcn.diplom.sql

import com.mcn.diplom.domain.SormTrunkRequest._
import com.mcn.diplom.domain._
import skunk._
import skunk.codec.all._

object codecsSormTrunk {

  val ormId: Codec[SormTrunkOrmId] =
    int4.imap(i => SormTrunkOrmId(i))(_.value)

  val name: Codec[SormTrunkName] =
    varchar(50).imap[SormTrunkName](i => SormTrunkName(i))(_.value)

  val regionId: Codec[SormTrunkRequestRegionId] =
    int4.imap(i => SormTrunkRequestRegionId(i))(_.value)

  val usType: Codec[SormTrunkRequestUsType] =
    int4.imap(i => SormTrunkRequestUsType(i))(_.value)

  val ormIdO: Codec[Option[SormTrunkOrmId]] =
    int4.opt.imap[Option[SormTrunkOrmId]](_.map(SormTrunkOrmId(_)))(_.map(_.value))

}
