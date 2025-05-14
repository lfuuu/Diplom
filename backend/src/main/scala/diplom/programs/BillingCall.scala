package com.mcn.diplom.programs

import cats.MonadThrow
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
import com.mcn.diplom.domain.nispd.BillingServiceTrunk._
import com.mcn.diplom.domain.nispd.BillingServiceNumber._

import com.mcn.diplom.lib.Time
import com.mcn.diplom.domain.nispd.BillingClient._

@nowarn
final case class BillingCall[F[_]: Logger: Time: MonadThrow](
  serviceClient: BillingClientsService[F],
  trunk: AuthTrunksService[F],
  serviceNumber: BillingServiceNumbersService[F],
  serviceTrunk: BillingServiceTrunksService[F]
) {

  def billing(cdr: CallCdr, orig: Boolean): EitherT[F, BillingCallError, CallRaw] = {

    def findTrunk: EitherT[F, BillingCallError, AuthTrunk] = {
      val trunkName = if (orig) cdr.srcRoute.value else cdr.dstRoute.value
      EitherT.fromOptionF(trunk.findByName(AuthTrunkName(trunkName)), ifNone = TrunkNotFound(s"Транк $trunkName не найден."))
    }

    def findServiceNumber(tm: Instant, num: BillingServiceNumberDID): EitherT[F, BillingCallError, BillingServiceNumber] =
      EitherT.fromOptionF(serviceNumber.findServiceNumberByNum(tm, num), ifNone = NumberNotFound(s"Номер $num никому не принадлежит."))

    def findServiceTrunk(tm: Instant, trunk: AuthTrunk): EitherT[F, BillingCallError, BillingServiceTrunk] =
      EitherT.fromOptionF(
        serviceTrunk.findServiceTrunkByTrunk(tm, BillingTrunkId(trunk.id.value)),
        ifNone = ServiceTrunkNotFound(s"К транку $trunk не подключен оператор")
      )

    def findClientById(clientId: BillingClientId): EitherT[F, BillingCallError, BillingClient] =
      EitherT.fromOptionF(
        serviceClient.findById(clientId),
        ifNone = ClientNotFound(s"К клиент #$clientId не найден")
      )

    def billingByNumber: EitherT[F, BillingCallError, CallRaw] =
      for {
        tm            <- EitherT.liftF(Time[F].getInstantNow)
        num            = BillingServiceNumberDID(if (orig) cdr.srcNumber.value else cdr.dstNumber.value)
        serviceNumber <- findServiceNumber(tm, num)
        client        <- findClientById(BillingClientId(serviceNumber.clientId.value))

      } yield CallRaw(
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

    def billingByTrunk(trunk: AuthTrunk): EitherT[F, BillingCallError, CallRaw] =
      for {
        tm            <- EitherT.liftF(Time[F].getInstantNow)
        serviceTrunks <- findServiceTrunk(tm, trunk)
        client        <- findClientById(BillingClientId(serviceTrunks.clientId.value))
      } yield CallRaw(
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

    for {
      trunk <- findTrunk
      raw   <- if (trunk.authByNumber.value) billingByNumber else billingByTrunk(trunk)
    } yield raw

  }
  // 1. вычисляем trunk
  // 2. Вычисляем услугу ( в зависимости от типа авторизации - транк или нумбер)
  // 3. Вычисляем клиента
  // 4. Вычисляем прайслист

}
