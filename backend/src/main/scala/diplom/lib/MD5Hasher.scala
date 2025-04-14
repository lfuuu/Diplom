package com.mcn.diplom.lib

import cats.effect.Sync
import cats.syntax.all._
import org.bouncycastle.jcajce.provider.digest.MD5

trait MD5Hasher[F[_]] {
  def hash(value: String): F[String]
  def hashBytes(bytes: Array[Byte]): F[String]
}

object MD5Hasher {

  def make[F[_]: Sync]() =
    Sync[F]
      .delay(new MD5.Digest())
      .map(md =>
        new MD5Hasher[F] {

          def hash(value: String)           = Sync[F].delay(md.digest(value.getBytes("Windows-1251")).map("%02x".format(_)).mkString)
          def hashBytes(bytes: Array[Byte]) = Sync[F].delay((new MD5.Digest()).digest(bytes).map("%02x".format(_)).mkString)

        }
      )

}
