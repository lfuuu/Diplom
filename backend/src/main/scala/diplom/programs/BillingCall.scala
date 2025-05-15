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
import com.mcn.diplom.domain.nispd.BillingServiceTrunk._
import com.mcn.diplom.domain.nispd.BillingServiceNumber._

import com.mcn.diplom.lib.Time
import com.mcn.diplom.domain.nispd.BillingClient._
import com.mcn.diplom.domain.nispd.BillingPricelist._
import com.mcn.diplom.services.BillingPacketsService
import com.mcn.diplom.domain.nispd.BillingPacket._
import com.mcn.diplom.services.BillingPricelistItemsService
import com.mcn.diplom.domain.nispd.BillingPricelistItem.BillingPriceListItemPricelistId
import com.mcn.diplom.domain.nispd.BillingPricelistItem.BillingPrice

@nowarn
final case class BillingCall[F[_]: Logger: Time: MonadThrow](
  serviceClient: BillingClientsService[F],
  trunk: AuthTrunksService[F],
  serviceNumber: BillingServiceNumbersService[F],
  serviceTrunk: BillingServiceTrunksService[F],
  servicePacket: BillingPacketsService[F],
  servicePricelistItems: BillingPricelistItemsService[F]
) {

  def billingLeg(cdr: CallCdr, orig: Boolean): EitherT[F, BillingCallError, CallRaw] = {

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

    def findPriceListsForServiceNumber(
      tm: Instant,
      serviceNumberId: BillingServiceNumberId
    ): EitherT[F, BillingCallError, List[BillingPacketPricelistId]] =
      EitherT.liftF(
        servicePacket.getPricelistsForServiceNumberId(BillingPacketServiceNumberId(serviceNumberId.value), tm)
      )

    def findPriceListsForServiceTrunk(
      tm: Instant,
      serviceTrunkId: BillingServiceTrunkId
    ): EitherT[F, BillingCallError, List[BillingPacketPricelistId]] =
      EitherT.liftF(
        servicePacket.getPricelistsForServiceTrunkId(BillingPacketServiceTrunkId(serviceTrunkId.value), tm)
      )

    def findBestPrice(
      tm: Instant,
      priceLists: List[BillingPacketPricelistId],
      numB: String
    ): EitherT[F, BillingCallError, (BillingPacketPricelistId, BillingPrice)] =
      EitherT.fromOptionF(
        priceLists
          .map(idPriceList =>
            servicePricelistItems.matchPrefix(tm, numB, BillingPriceListItemPricelistId(idPriceList.value)).map(price => (idPriceList, price))
          )
          .sequence
          .map(_.filter(_._2.isDefined).map(v => (v._1, v._2.get)).sortBy(-_._2.value.abs).headOption),
        ifNone = PricelistNotFound(s"К номераВ  #$numB не найден подходящий прайслист")
      )

    def billingByNumber(numB: DstNumber): EitherT[F, BillingCallError, CallRaw] =
      for {
        tm            <- EitherT.liftF(Time[F].getInstantNow)
        num            = BillingServiceNumberDID(if (orig) cdr.srcNumber.value else cdr.dstNumber.value)
        serviceNumber <- findServiceNumber(tm, num)
        client        <- findClientById(BillingClientId(serviceNumber.clientId.value))
        pricelistIds  <- findPriceListsForServiceNumber(tm, serviceNumber.id)
        price         <- findBestPrice(tm, pricelistIds, numB.value)

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

    def billingByTrunk(trunk: AuthTrunk, numB: DstNumber): EitherT[F, BillingCallError, CallRaw] =
      for {
        tm           <- EitherT.liftF(Time[F].getInstantNow)
        serviceTrunk <- findServiceTrunk(tm, trunk)
        client       <- findClientById(BillingClientId(serviceTrunk.clientId.value))
        pricelistIds <- findPriceListsForServiceTrunk(tm, serviceTrunk.id)
        price        <- findBestPrice(tm, pricelistIds, numB.value)
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
      raw   <- if (trunk.authByNumber.value) billingByNumber(cdr.dstNumber) else billingByTrunk(trunk, cdr.dstNumber)
    } yield raw

  }
  // 1. вычисляем trunk
  // 2. Вычисляем услугу ( в зависимости от типа авторизации - транк или нумбер)
  // 3. Вычисляем клиента
  // 4. Вычисляем прайслист

  // Делаем функцию расчета по прайслисту.
  // применяем пакеты

}
