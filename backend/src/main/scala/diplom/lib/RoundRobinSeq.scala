package com.mcn.diplom.lib

import cats.Monad
import cats.effect.kernel.{ Concurrent, Ref }
import cats.syntax.all._

trait RoundRobinSeq[F[_], T] {
  def getNextStatus: F[T]
}

class RoundRobinSeqImpl[F[_]: Monad, T](
  ref: Ref[F, Vector[T]]
) extends RoundRobinSeq[F, T] {

  def getNextStatus: F[T] =
    for {
      status <- ref.getAndUpdate(i => RoundRobinSeq.roundRobin(i))
    } yield status.headOption.get
}

object RoundRobinSeq {

  def roundRobin[T]: Vector[T] => Vector[T] =
    vector => if (vector.isEmpty) vector else vector.tail :+ vector.head

  def make[F[_]: Concurrent, T](responses: Seq[T]): F[RoundRobinSeq[F, T]] =
    Ref.of[F, Vector[T]](Vector.from(responses.toSeq)).map(new RoundRobinSeqImpl[F, T](_))

}
