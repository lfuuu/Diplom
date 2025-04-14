package com.mcn.diplom.lib

import java.time.Instant

import cats.effect.Sync
import com.mcn.diplom.domain.misc.Timestamp

trait Time[F[_]] {
  def timestamp: F[Timestamp]
  def getEpochSecond: F[Long]
  def getEpochMilli: F[String]
  def getInstantNow: F[Instant]
}

object Time {
  def apply[F[_]](implicit ev: Time[F]): Time[F] = ev

  implicit def syncInstance[F[_]: Sync]: Time[F] =
    new Time[F] {
      def timestamp: F[Timestamp] = Sync[F].delay(Timestamp(Instant.now()))
      def getEpochSecond: F[Long] = Sync[F].delay(Instant.now().getEpochSecond())

      def getInstantNow: F[Instant] = Sync[F].delay(Instant.now())
      def getEpochMilli: F[String]  = Sync[F].delay(f"${System.currentTimeMillis().toDouble / 1000}%.3f")
    }
}
