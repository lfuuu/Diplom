package com.mcn.diplom.lib

import cats.effect.Sync
import cats.syntax.all._
import fs2.io.file.{ Files, Path }
import fs2.{ Stream, text }

trait PidFile[F[_]] {
  def create(fn: Path): F[Unit]
}

object PidFile {

  def make[F[_]: Sync: Files]: PidFile[F] =
    new PidFile[F] {

      def create(fn: Path): F[Unit] =
        for {
          procid <- Sync[F].delay(ProcessHandle.current().pid())
          _      <- Stream
                 .emit(procid.toString)
                 .through(text.utf8.encode)
                 .through(Files[F].writeAll(fn))
                 .compile
                 .drain
        } yield Sync[F]

    }
}
