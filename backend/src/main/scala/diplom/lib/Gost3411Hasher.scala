package com.mcn.diplom.lib

import cats.effect.Sync
import cats.syntax.all._
import org.bouncycastle.jcajce.provider.digest.GOST3411

trait Gost3411Hasher {
  def hash(value: String): String
}

object Gost3411Hasher {

  def make[F[_]: Sync]() =
    Sync[F]
      .delay(new GOST3411.Digest2012_256())
      .map(md =>
        new Gost3411Hasher {

          def hash(value: String): String = md.digest(value.getBytes("Windows-1251")).map("%02x".format(_)).mkString

        }
      )

}
