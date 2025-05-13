package com.mcn.diplom.programs

import cats.MonadThrow
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import com.mcn.diplom.services.AuthTrunksService
import com.mcn.diplom.services.BillingServiceNumbersService
import com.mcn.diplom.services.BillingServiceTrunksService
import com.mcn.diplom.services.BillingClientsService
import com.mcn.diplom.domain.calls.CallCdr._
import com.mcn.diplom.domain.calls.CallRaw._
import scala.annotation.nowarn
import java.time.Instant
import com.mcn.diplom.domain.auth.AuthTrunk._
import cats.data.EitherT
import com.mcn.diplom.domain.nispd.BillingCallError._
import com.mcn.diplom.domain.nispd

@nowarn
final case class BillingCall[F[_]: Logger: MonadThrow](
  client: BillingClientsService[F],
  trunk: AuthTrunksService[F],
  serviceNumber: BillingServiceNumbersService[F],
  serviceTrunk: BillingServiceTrunksService[F]
) {

  def findTrunk(cdr: CallCdr, orig: Boolean): EitherT[F, BillingCallError, AuthTrunk] = {
    val trunkName = if (orig) cdr.srcRoute.value else cdr.dstRoute.value
    EitherT.fromOptionF(trunk.findByName(AuthTrunkName(trunkName)), ifNone = TrunkNotFound(s"Транк $trunkName не найден."))
  }

  def billing(cdr: CallCdr, orig: Boolean) = {

    def billingByNumber(trunk: AuthTrunk): EitherT[F, BillingCallError, CallRaw] = for {
        serviceNumber <- findServiceNumber()

    } yield      CallRaw(
      id = CallRawId(1L),
      orig = CallRawOrig(true),
      peerId = CallRawPeerId(123L),
      cdrId = CallRawCdrId(456L),
      connectTime = CallRawConnectTime(Instant.parse("2023-10-01T12:00:00Z")),
      trunkId = CallRawTrunkId(789),
      clientId = CallRawClientId(10),
      serviceTrunkId = CallRawServiceTrunkId(11),
      serviceNumberId = CallRawServiceNumberId(12),
      srcNumber = CallRawSrcNumber("+1234567890"),
      dstNumber = CallRawDstNumber("+0987654321"),
      billedTime = CallRawBilledTime(60),
      rate = CallRawRate(BigDecimal("0.05")),
      cost = CallRawCost(BigDecimal("3.00")),
      pricelistId = CallRawPricelistId(42),
      disconnectCause = CallRawDisconnectCause(0.toShort)
    )


    def billingByTrunk(trunk: AuthTrunk): EitherT[F, BillingCallError, CallRaw] = ???

    for {
      trunk <- findTrunk(cdr, orig)
      raw   <- if (trunk.authByNumber.value) billingByNumber(trunk) else billingByTrunk(trunk)
    } yield raw

    // CallRaw(
    //   id = CallRawId(1L),
    //   orig = CallRawOrig(true),
    //   peerId = CallRawPeerId(123L),
    //   cdrId = CallRawCdrId(456L),
    //   connectTime = CallRawConnectTime(Instant.parse("2023-10-01T12:00:00Z")),
    //   trunkId = CallRawTrunkId(789),
    //   clientId = CallRawClientId(10),
    //   serviceTrunkId = CallRawServiceTrunkId(11),
    //   serviceNumberId = CallRawServiceNumberId(12),
    //   srcNumber = CallRawSrcNumber("+1234567890"),
    //   dstNumber = CallRawDstNumber("+0987654321"),
    //   billedTime = CallRawBilledTime(60),
    //   rate = CallRawRate(BigDecimal("0.05")),
    //   cost = CallRawCost(BigDecimal("3.00")),
    //   pricelistId = CallRawPricelistId(42),
    //   disconnectCause = CallRawDisconnectCause(0.toShort)
    // )
  }
  // 1. вычисляем trunk
  // 2. Вычисляем услугу ( в зависимости от типа авторизации - транк или нумбер)
  // 3. Вычисляем клиента
  // 4. Вычисляем прайслист

}
