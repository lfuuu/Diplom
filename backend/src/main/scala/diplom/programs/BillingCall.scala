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

  def billingLeg(cdr: CallCdr, orig: Boolean): EitherT[F, BillingCallError, CallRawCreateRequest] = {

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
      serviceNumberId: BillingServiceNumberId,
      orig: Boolean
    ): EitherT[F, BillingCallError, List[BillingPacketPricelistId]] =
      EitherT.liftF(
        servicePacket.getPricelistsForServiceNumberId(BillingPacketServiceNumberId(serviceNumberId.value), tm, BillingPacketOrig(orig))
      )

    def findPriceListsForServiceTrunk(
      tm: Instant,
      serviceTrunkId: BillingServiceTrunkId,
      orig: Boolean
    ): EitherT[F, BillingCallError, List[BillingPacketPricelistId]] =
      EitherT.liftF(
        servicePacket.getPricelistsForServiceTrunkId(BillingPacketServiceTrunkId(serviceTrunkId.value), tm, BillingPacketOrig(orig))
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

    def billingByNumber(trunk: AuthTrunk, numB: DstNumber): EitherT[F, BillingCallError, CallRawCreateRequest] =
      for {
        tm            <- EitherT.liftF(Time[F].getInstantNow)
        num            = BillingServiceNumberDID(if (orig) cdr.srcNumber.value else cdr.dstNumber.value)
        serviceNumber <- findServiceNumber(tm, num)
        client        <- findClientById(BillingClientId(serviceNumber.clientId.value))
        pricelistIds  <- findPriceListsForServiceNumber(tm, serviceNumber.id, orig)
        price         <- findBestPrice(tm, pricelistIds, numB.value)

      } yield CallRawCreateRequest(
        orig = CallRawOrig(orig),
        peerId = CallRawPeerId(-1),
        cdrId = CallRawCdrId(cdr.id.value),
        connectTime = CallRawConnectTime(cdr.connectTime.value),
        trunkId = CallRawTrunkId(trunk.id.value),
        clientId = CallRawClientId(client.id.value),
        serviceTrunkId = None,
        serviceNumberId = CallRawServiceNumberId(serviceNumber.id.value).some,
        srcNumber = CallRawSrcNumber(cdr.srcNumber.value),
        dstNumber = CallRawDstNumber(cdr.dstNumber.value),
        billedTime = CallRawBilledTime(60),
        rate = CallRawRate(price._2.value),
        cost = CallRawCost(price._2.value * cdr.sessionTime.value),
        pricelistId = CallRawPricelistId(price._1.value),
        disconnectCause = CallRawDisconnectCause(cdr.disconnectCause.value)
      )

    def billingByTrunk(trunk: AuthTrunk, numB: DstNumber): EitherT[F, BillingCallError, CallRawCreateRequest] =
      for {
        tm           <- EitherT.liftF(Time[F].getInstantNow)
        serviceTrunk <- findServiceTrunk(tm, trunk)
        client       <- findClientById(BillingClientId(serviceTrunk.clientId.value))
        pricelistIds <- findPriceListsForServiceTrunk(tm, serviceTrunk.id, orig)
        price        <- findBestPrice(tm, pricelistIds, numB.value)

      } yield CallRawCreateRequest(
        orig = CallRawOrig(orig),
        peerId = CallRawPeerId(-1),
        cdrId = CallRawCdrId(cdr.id.value),
        connectTime = CallRawConnectTime(cdr.connectTime.value),
        trunkId = CallRawTrunkId(trunk.id.value),
        clientId = CallRawClientId(client.id.value),
        serviceTrunkId = CallRawServiceTrunkId(serviceTrunk.id.value).some,
        serviceNumberId = None,
        srcNumber = CallRawSrcNumber(cdr.srcNumber.value),
        dstNumber = CallRawDstNumber(cdr.dstNumber.value),
        billedTime = CallRawBilledTime(60),
        rate = CallRawRate(price._2.value),
        cost = CallRawCost(price._2.value * cdr.sessionTime.value),
        pricelistId = CallRawPricelistId(price._1.value),
        disconnectCause = CallRawDisconnectCause(cdr.disconnectCause.value)
      )

    for {
      trunk <- findTrunk
      raw   <- if (trunk.authByNumber.value) billingByNumber(trunk, cdr.dstNumber) else billingByTrunk(trunk, cdr.dstNumber)
    } yield raw

  }
  // 1. вычисляем trunk
  // 2. Вычисляем услугу ( в зависимости от типа авторизации - транк или нумбер)
  // 3. Вычисляем клиента
  // 4. Вычисляем прайслист

  // Делаем функцию расчета по прайслисту.
  // применяем пакеты

}
