package com.mcn.diplom.lib

import cats.Applicative
import cats.effect.kernel.{ Async, Ref, Resource }
import cats.effect.std.Queue
import cats.syntax.all._
import fs2.kafka.{ KafkaProducer, ProducerSettings }

trait Producer[F[_], A] {
  def send(a: A): F[Unit]
  def send(a: A, properties: Map[String, String]): F[Unit]
}

object Producer {

  def local[F[_]: Applicative, A](queue: Queue[F, Option[A]]): Resource[F, Producer[F, A]] =
    Resource.make[F, Producer[F, A]](
      Applicative[F].pure(
        new Producer[F, A] {
          def send(a: A): F[Unit]                                  = queue.offer(Some(a))
          def send(a: A, properties: Map[String, String]): F[Unit] = send(a)
        }
      )
    )(_ => queue.offer(None))

  class Dummy[F[_]: Applicative, A] extends Producer[F, A] {
    def send(a: A): F[Unit]                                  = Applicative[F].unit
    def send(a: A, properties: Map[String, String]): F[Unit] = send(a)
  }

  //def dummy[F[_]: Applicative, A]: Producer[F, A] = Dummy[F, A]

  def testMany[F[_]: Applicative, A](ref: Ref[F, List[A]]): Producer[F, A] =
    new Dummy[F, A] {
      override def send(a: A): F[Unit] = ref.update(_ :+ a)
    }

  def test[F[_]: Applicative, A](ref: Ref[F, Option[A]]): Producer[F, A] =
    new Dummy[F, A] {
      override def send(a: A): F[Unit] = ref.set(Some(a))
    }

  // def dummySeqIdMaker[F[_]: Applicative, A]: SeqIdMaker[F, A] = new SeqIdMaker[F, A] {
  //   def make(lastSeqId: Long, currentMsg: A): F[Long] = Applicative[F].pure(0L)
  // }

  def kafka[F[_]: Async, A](
    settings: ProducerSettings[F, String, A],
    topic: String
  ): Resource[F, Producer[F, A]] =
    KafkaProducer.resource(settings).map { p =>
      new Dummy[F, A] {
        override def send(a: A): F[Unit] =
          p.produceOne_(topic, "key", a).flatten.void
      }
    }
}
