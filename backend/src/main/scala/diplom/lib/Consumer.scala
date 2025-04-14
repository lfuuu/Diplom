package com.mcn.diplom.lib

import scala.concurrent.duration._

import cats.Applicative
import cats.effect.kernel._
import cats.effect.std.Queue
import cats.effect.syntax.all._
import cats.syntax.all._
import fs2.Stream
import fs2.kafka._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{ Logger, SelfAwareStructuredLogger }

trait Consumer[F[_], A] extends Acker[F, A] {
  def receiveM: Stream[F, Consumer.Msg[A]]
  def receiveM(id: Consumer.MsgId): Stream[F, Consumer.Msg[A]]
  def receive: Stream[F, A]
  def lastMsgId: F[Option[Consumer.MsgId]]
  def gracefulShutdownWraper(action: F[Unit]): F[Unit]
}

object Consumer {

  // Pulsar: MessageId(ledgerId: Long, entryId: Long, partitionIndex: Int)

  type MessageId = String

  sealed trait MsgId
  case object Dummy          extends MsgId
  case class Kafka[K](id: K) extends MsgId

  type Properties = Map[String, String]

  // object MsgId {
  //   def earliest: MsgId          = MsgId.Pulsar(MessageId.earliest)
  //   def latest: MsgId            = MsgId.Pulsar(MessageId.latest)
  //   def from(str: String): MsgId = MsgId.Pulsar(MessageId.fromByteArray(str.getBytes(UTF_16BE)))
  // }

  final case class Msg[A](id: MsgId, props: Properties, payload: A)

  def local[F[_]: Applicative, A](
    queue: Queue[F, Option[A]]
  ): Consumer[F, A] =
    new Consumer[F, A] {
      def receiveM: Stream[F, Msg[A]]            = receive.map(Msg(Dummy, Map.empty, _))
      def receiveM(id: MsgId): Stream[F, Msg[A]] = receiveM
      def receive: Stream[F, A]                  = Stream.fromQueueNoneTerminated(queue)
      def ack(id: MsgId): F[Unit]                = Applicative[F].unit
      def ack(ids: Set[MsgId]): F[Unit]          = Applicative[F].unit
      def nack(id: MsgId): F[Unit]               = Applicative[F].unit
      def lastMsgId: F[Option[MsgId]]            = none.pure[F]

      def gracefulShutdownWraper(action: F[Unit]) = action.void
    }

  def kafka[F[_]: Async: Logger, A](
    settings: ConsumerSettings[F, String, A],
    topic: String,
    gracefulShutdownTimeout: FiniteDuration
  ): Resource[F, Consumer[F, A]] =
    Resource.eval(Ref.of[F, List[CommittableOffset[F]]](List.empty)).flatMap { ref =>
      KafkaConsumer
        .resource[F, String, A](settings.withEnableAutoCommit(false))
        .evalTap(_.subscribeTo(topic))
        .map { c =>
          new Consumer[F, A] {
            def receiveM: Stream[F, Msg[A]] =
              c.stream.evalMap(c => ref.update(_ :+ c.offset).as(Msg(Kafka(c.offset), Map.empty, c.record.value)))

            def receiveM(id: MsgId): Stream[F, Consumer.Msg[A]] = receiveM

            def receive: Stream[F, A] =
              c.stream.flatMap { s =>
                Stream
                  .emit(s.offset)
                  .through(fs2.kafka.commitBatchWithin[F](500, 10.seconds))
                  .as(s.record.value)
              }

            def ack(ids: Set[MsgId]): F[Unit] =
              ref.get.flatMap(CommittableOffsetBatch.fromFoldable(_).commit)

            def lastMsgId: F[Option[MsgId]] = none.pure[F]
            def ack(id: MsgId): F[Unit]     = ack(Set(id))
            def nack(id: MsgId): F[Unit]    = Applicative[F].unit

            def gracefulShutdownWraper(action: F[Unit]) = Consumer.gracefulShutdown(c, gracefulShutdownTimeout)(action).void
          }
        }
    }

  private def gracefulShutdown[F[_]: Async: Logger](consumer: KafkaConsumer[F, _, _], gracefulShutdownTimeout: FiniteDuration)(f: F[_]): F[Unit] = {
    implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger
    Async[F].uncancelable { poll =>
      for {
        runFiber <- Async[F].start(f)
        _        <- poll(runFiber.join).onCancel {
               for {
                 _               <- Logger[F].debug("Starting graceful shutdown")
                 _               <- consumer.stopConsuming
                 shutdownOutcome <- runFiber.join
                                      .timeoutTo(
                                        gracefulShutdownTimeout,
                                        Async[F].pure(
                                          Outcome.Errored(new RuntimeException("Graceful shutdown timed out"))
                                        )
                                      )
                 _               <- shutdownOutcome match {
                        case Outcome.Succeeded(_) =>
                          Logger[F].debug("Succeeded in graceful shutdown")
                        case Outcome.Canceled()   =>
                          Logger[F].error("Canceled in graceful shutdown") >> runFiber.cancel
                        case Outcome.Errored(e)   =>
                          Logger[F].error("Failed to shutdown gracefully") >> runFiber.cancel >> Async[F].raiseError(e)
                      }
               } yield Async[F]
             }
      } yield ()
    }
  }

}
