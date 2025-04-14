package com.mcn.diplom.services

import cats.Applicative
import cats.effect._
import cats.syntax.all._
import com.mcn.diplom.domain.Cdr
import com.mcn.diplom.domain.CdrAccountStatusTypeEnum._
import com.mcn.diplom.retries.Retry
import com.mcn.diplom.sql.codecs._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{ Logger, SelfAwareStructuredLogger }
import skunk._
import skunk.implicits._

trait CdrStorage[F[_]] {
  def addCdr(cdr: Cdr): F[Unit]
}

object CdrStorage {

  import CdrStorageSeriveSQL._

  def make[F[_]: Applicative: Sync: Logger: Retry](
    postgres: Resource[F, Session[F]]
  ): CdrStorage[F] =
    new CdrStorage[F] {

      implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger

      def addCdr(cdr: Cdr) =
        cdr match {
          case cdr if cdr.accountStatusType == START =>
            for {
              _   <- Logger[F].debug(s"start: ${cdr.toString}")
              f    = addStartCmd(cdr)
              q    = f.fragment.query(void)
              res <- postgres.use(_.prepare(q).flatMap(_.option(f.argument)).void)
            } yield res

          case cdr if cdr.accountStatusType == STOP  =>
            for {
              _   <- Logger[F].debug(s"stop: ${cdr.toString}")
              f    = addStopCmd(cdr)
              q    = f.fragment.query(void)
              res <- postgres.use(_.prepare(q).flatMap(_.option(f.argument)).void)
            } yield res
          case _                                     => Applicative[F].unit

        }
    }

  object CdrStorageSeriveSQL {

    import com.mcn.diplom.sql.codecsTele2Abonent._

    val startEnc =
      callId *: cdrNasIp *: cdrSrcNumberO *: cdrDstNumberO *: cdrRedirectNumberO *: cdrSetupTimeO *: cdrConnectTimeO *:
        cdrSrcRouteO *: cdrDstRouteO *: cdrSrcNoaO *: cdrDstNoaO *: cdrMCNCallIdO *: cdrDstReplaceO *: cdrRedirectNoaO *:
        cdrOutgoingRedirectNumberO *: cdrOutgoingRedirectNumberNoaO *: cdrOcpnNumberO *: cdrOcpnNumberNoaO *: cdrCpcO

    val stopEnc =
      callId *: cdrNasIp *: cdrSrcNumberO *: cdrDstNumberO *: cdrRedirectNumberO *: cdrSessionTimeO *: cdrSetupTimeO *: cdrConnectTimeO *:
        CdrDisconnectTimeO *: cdrDisconnectCauseO *: cdrSrcRouteO *: cdrDstRouteO *: cdrSrcNoaO *: cdrDstNoaO *: cdrDstReplaceO *: cdrCallFinishedO *: cdrReleasingPartyO *:
        cdrInSigCallIdO *: cdrOutSigCallIdO *: cdrMCNCallIdO *: cdrRedirectNoaO *: cdrOutgoingRedirectNumberO *: cdrOutgoingRedirectNumberNoaO *:
        cdrSessionTimePreciseO *: cdrOcpnNumberO *: cdrOcpnNumberNoaO *: cdrCpcO

    val addStartCmd = sql"select insert_start ($startEnc)"

    val addStopCmd = sql"select insert_cdr2 ($stopEnc)"

    def addStartCmd(c: Cdr): AppliedFragment =
      addStartCmd(
        c.callId *: c.nasIp *: c.srcNumber *: c.dstNumber *: c.redirectNumber *:
          c.setupTime *: c.connectTime *: c.srcRoute *: c.dstRoute *: c.srcNoa *: c.dstNoa *: c.mcnCallId *: c.dstReplace *: c.redirectNoa *:
          c.outgoingRedirectNumber *: c.outgoingRedirectNumberNoa *: c.ocpnNumber *: c.ocpnNumberNoa *: c.cpc *: EmptyTuple
      )

    def addStopCmd(c: Cdr): AppliedFragment =
      addStopCmd(
        c.callId *: c.nasIp *: c.srcNumber *: c.dstNumber *: c.redirectNumber *: c.sessionTime *: c.setupTime *: c.connectTime *:
          c.disconnectTime *: c.disconnectCause *: c.srcRoute *: c.dstRoute *: c.srcNoa *: c.dstNoa *: c.dstReplace *: c.callFinished *: c.releasingParty *:
          c.inSigCallId *: c.outSigCallId *: c.mcnCallId *: c.redirectNoa *: c.outgoingRedirectNumber *: c.outgoingRedirectNumberNoa *:
          c.sessionTimePrecise *: c.ocpnNumber *: c.ocpnNumberNoa *: c.cpc *: EmptyTuple
      )

  }
}
