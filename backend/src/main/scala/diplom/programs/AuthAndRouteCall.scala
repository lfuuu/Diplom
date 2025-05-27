package com.mcn.diplom.programs

import scala.annotation.nowarn

import cats.MonadThrow
import cats.syntax.all._
import cats.data.EitherT
import com.mcn.diplom.domain.nispd.AuthRequest.AuthRequest
import com.mcn.diplom.domain.nispd.AuthRequestError.AuthRequestError
import com.mcn.diplom.domain.nispd.AuthResponse.AuthResponse
import com.mcn.diplom.lib.Time
import com.mcn.diplom.services._
import org.typelevel.log4cats.Logger
import com.mcn.diplom.domain.nispd.AuthRequestError._
import com.mcn.diplom.domain.auth.AuthTrunk.AuthTrunk
import com.mcn.diplom.domain.auth.AuthTrunk.AuthTrunkName
import com.mcn.diplom.domain.nispd.AuthRequestError.SrcTrunkNotFound
import com.mcn.diplom.domain.nispd.AuthResponse._
import com.mcn.diplom.domain.nispd.AuthRequest._
import com.mcn.diplom.domain.nispd.BillingServiceNumber._
import java.time.Instant
import com.mcn.diplom.domain.nispd.BillingServiceTrunk._
import com.mcn.diplom.domain.nispd.BillingClient._
import com.mcn.diplom.domain.calls.CallCdr._
import com.mcn.diplom.domain.calls.CallRaw.CallRawCreateRequest
import com.mcn.diplom.domain.auth.AuthTrunk.AuthTrunkId

@nowarn
final case class AuthAndRouteCall[F[_]: Logger: Time: MonadThrow](
  billingCall: BillingCall[F],
  serviceClient: BillingClientsService[F],
  trunk: AuthTrunksService[F],
  serviceNumber: BillingServiceNumbersService[F],
  serviceTrunk: BillingServiceTrunksService[F],
  servicePacket: BillingPacketsService[F],
  servicePricelistItems: BillingPricelistItemsService[F]
) {

  def doAuthAndRoute(req: AuthRequest): EitherT[F, AuthRequestError, AuthResponse] = {

    def findTrunk: EitherT[F, AuthRequestError, AuthTrunk] = {
      val trunkName = req.srcRoute.value
      EitherT.fromOptionF(trunk.findByName(AuthTrunkName(trunkName)), ifNone = SrcTrunkNotFound(s"Транк $trunkName не найден."))
    }

    def findServiceNumber(tm: Instant, num: BillingServiceNumberDID): EitherT[F, AuthRequestError, BillingServiceNumber] =
      EitherT.fromOptionF(serviceNumber.findServiceNumberByNum(tm, num), ifNone = NumberNotFound(s"Номер $num никому не принадлежит."))

    def findServiceTrunk(tm: Instant, trunk: AuthTrunk): EitherT[F, AuthRequestError, BillingServiceTrunk] =
      EitherT.fromOptionF(
        serviceTrunk.findServiceTrunkByTrunk(tm, BillingTrunkId(trunk.id.value)),
        ifNone = ServiceTrunkNotFound(s"К транку $trunk не подключен оператор")
      )

    def findClientById(clientId: BillingClientId): EitherT[F, AuthRequestError, BillingClient] =
      EitherT.fromOptionF(
        serviceClient.findById(clientId),
        ifNone = ClientNotFound(s"К клиент #$clientId не найден")
      )

    def billingByNumber(trunk: AuthTrunk, numB: AuthRequestDstNumber): EitherT[F, AuthRequestError, BillingClient] =
      for {
        _             <- EitherT.liftF(Logger[F].debug(s"billingByNumber: numB=${numB.toString}"))
        tm            <- EitherT.liftF(Time[F].getInstantNow)
        num            = BillingServiceNumberDID(req.srcNumber.value)
        _             <- EitherT.liftF(Logger[F].debug(s"num=${num.toString}"))
        serviceNumber <- findServiceNumber(tm, num)
        _             <- EitherT.liftF(Logger[F].debug(s"serviceNumber=${serviceNumber.toString}"))
        client        <- findClientById(BillingClientId(serviceNumber.clientId.value))
        _             <- EitherT.liftF(Logger[F].debug(s"client=${client.toString}"))

      } yield client

    def billingByTrunk(trunk: AuthTrunk, numB: AuthRequestDstNumber): EitherT[F, AuthRequestError, BillingClient] =
      for {
        tm           <- EitherT.liftF(Time[F].getInstantNow)
        serviceTrunk <- findServiceTrunk(tm, trunk)
        client       <- findClientById(BillingClientId(serviceTrunk.clientId.value))
      } yield client

    def getAllTermTrunk(origTrunk: SrcRoute): F[List[AuthTrunk]] =
      trunk.findAll.map(_.filterNot(_.trunkName.value == origTrunk.value))

    def getPossibleTermCdr(origCdr: CallCdr) =
      getAllTermTrunk(origCdr.srcRoute).map(
        _.map(termTrunk => origCdr.copy(dstRoute = DstRoute(termTrunk.trunkName.value)))
      )

    def getDestTrunks(origCdr: CallCdr) =
      EitherT.liftF[F, AuthRequestError, List[CallRawCreateRequest]](getPossibleTermCdr(origCdr).flatMap { termCdrs =>
        termCdrs
          .traverse(termCdr => billingCall.billingLeg(termCdr, false).value)
          .map(_.collect { case Right(request) => request })
      })

    def getBestTermTrunk(possibleRaws: List[CallRawCreateRequest]): EitherT[F, AuthRequestError, AuthTrunk] =
      EitherT.fromOptionF(
        possibleRaws
          .sortBy(-_.rate.value.abs)
          .headOption
          .map(_.trunkId)
          .map(trunkId => trunk.findById(AuthTrunkId(trunkId.value)))
          .getOrElse((None: Option[AuthTrunk]).pure),
        ifNone = RouteNotFound(s"Маршрут не найден")
      )

    def isBlocked(client: BillingClient) =
      EitherT.fromEither(client.isBlocked.value match {
        case true  => Left(ClientIsBlocked(s"Клиент #${client.id} Заблокирован"))
        case false => Right(false)
      })

    for {
      tm        <- EitherT.liftF(Time[F].getInstantNow)
      trunk     <- findTrunk
      _         <- EitherT.liftF(Logger[F].debug(s"trunk=${trunk.toString}"))
      client    <- if (trunk.authByNumber.value) billingByNumber(trunk, req.dstNumber) else billingByTrunk(trunk, req.dstNumber)
      _         <- isBlocked(client)
      origCdr    = CallCdr(
                  CallCdrId(-1),
                  CallId(-1),
                  SrcNumber(req.srcNumber.value),
                  DstNumber(req.dstNumber.value),
                  SetupTime(tm).some,
                  ConnectTime(tm),
                  DisconnectTime(tm).some,
                  SessionTime(60),
                  DisconnectCause(16),
                  SrcRoute(trunk.trunkName.value),
                  DstRoute("unknow")
                )
      _         <- EitherT.liftF(Logger[F].debug(s"origCdr=${origCdr.toString}"))
      origRaw   <- billingCall
                   .billingLeg(origCdr, true)
                   .leftMap(err => OrigLegBillError(s"Не смог протарифицировать оригинационное плечо: ${err.toString}"): AuthRequestError)
      _         <- EitherT.liftF(Logger[F].debug(s"origRaw=${origRaw.toString}"))
      destTrunk <- getDestTrunks(origCdr)
      bestTrunk <- getBestTermTrunk(destTrunk)

    } yield AuthResponse(dstRoute = AuthResponseDstRoute(bestTrunk.trunkName.value))

  }

}

// 1. Вычилслить клиента по оригинационному транку и режим авторизации.
// 2. расчитать клиента
// 3. проверить блокировку
// 4. расчитать стоимость оринационного плеча
// 5. расчитать возможные маршруты терминационные
// 6. выбрать лучший.
