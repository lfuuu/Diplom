package com.mcn.diplom.sql

import java.time.ZoneOffset

import cats.syntax.all._
import com.comcast.ip4s.Ipv4Address
import com.mcn.diplom.domain.Auth._
import com.mcn.diplom.domain.misc._
import skunk.Codec
import skunk.codec.all._
import skunk.data.Type

object codecs {

  val timestamp: Codec[Timestamp] = timestamptz.imap(t => Timestamp(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))

  val userId: Codec[UserId]     = uuid.imap[UserId](UserId(_))(_.value)
  val userName: Codec[UserName] = text.imap[UserName](UserName(_))(_.value)

  val encPassword: Codec[EncryptedPassword] = text.imap[EncryptedPassword](EncryptedPassword(_))(_.value)

  val void: Codec[String] = Codec.simple(_.toString, s => s.asRight[String], Type.void)

  val inet: Codec[Ipv4Address] =
    Codec.simple[Ipv4Address](
      u => u.toString,
      s =>
        Ipv4Address.fromString(s) match {
          case Some(x) => Right(x)
          case None    => Left(s"invalid ipv4 address: ${s}")
        },
      Type.inet
    )

}
