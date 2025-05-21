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
import com.mcn.diplom.domain.nispd.AuthRequestError.AccessReject

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

  def doAuthAndRoute(req: AuthRequest): EitherT[F, AuthRequestError, AuthResponse] =
    EitherT.fromOptionF((None: Option[AuthResponse]).pure[F], ifNone = AccessReject("Доступ запрещен"))

}
