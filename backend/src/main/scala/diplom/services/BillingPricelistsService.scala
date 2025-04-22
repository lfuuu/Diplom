package com.mcn.diplom.services

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.nispd.BillingPricelist._

trait BillingPricelistsService[F[_]] {
  def create(pricelist: BillingPricelistCreateRequest): F[Option[BillingPricelistId]]
  def findAll: F[List[BillingPricelist]]
  def findById(id: BillingPricelistId): F[Option[BillingPricelist]]
  def deleteById(id: BillingPricelistId): F[Unit]
}

object BillingPricelistsService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): BillingPricelistsService[F] =
    new BillingPricelistsService[F] {
      import BillingPricelistsSQL._

      override def findAll: F[List[BillingPricelist]] =
        postgres.use(_.execute(selectAll))

      override def create(request: BillingPricelistCreateRequest): F[Option[BillingPricelistId]] =
        postgres.use { session =>
          session.prepare(insertPricelist).flatMap(_.option(request))
        }

      override def findById(id: BillingPricelistId): F[Option[BillingPricelist]] =
        postgres.use { session =>
          session.prepare(findByIdPricelist).flatMap(_.option(id))
        }

      override def deleteById(id: BillingPricelistId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteByIdPricelist)(id).void
        }
    }
}

private object BillingPricelistsSQL {

  val id: Codec[BillingPricelistId]                 = int4.imap(BillingPricelistId(_))(_.value)

  val name: Codec[BillingPricelistName]             =
    text.imap(BillingPricelistName(_))(_.value)

  val dateFrom: Codec[BillingPricelistDateFrom]     =
    date.imap(BillingPricelistDateFrom(_))(_.value)

  val dateTo: Codec[Option[BillingPricelistDateTo]] =
    date.opt.imap(_.map(BillingPricelistDateTo(_)))(_.map(_.value))

  val roundToSec: Codec[BillingPricelistRoundToSec] =
    bool.imap(BillingPricelistRoundToSec(_))(_.value)

  val findAllCodec       = id *: name *: dateFrom *: dateTo *: roundToSec
  val createRequestCodec = name *: dateFrom *: dateTo *: roundToSec

  val selectAll: Query[Void, BillingPricelist] =
    sql"""
      SELECT id, name, date_from, date_to, round_to_sec
      FROM billing.pricelist
    """.query(findAllCodec).to[BillingPricelist]

  val insertPricelist: Query[BillingPricelistCreateRequest, BillingPricelistId] =
    sql"""
      INSERT INTO billing.pricelist (name, date_from, date_to, round_to_sec)
      VALUES ($name, $dateFrom, $dateTo, $roundToSec)
      RETURNING id
    """
      .query(int4)
      .contramap[BillingPricelistCreateRequest] { req =>
        req.name *: req.dateFrom *: req.dateTo *: req.roundToSec *: EmptyTuple
      }
      .map(BillingPricelistId(_))

  val findByIdPricelist: Query[BillingPricelistId, BillingPricelist] =
    sql"""
      SELECT id, name, date_from, date_to, round_to_sec
      FROM billing.pricelist
      WHERE id = $id
    """.query(findAllCodec).to[BillingPricelist]

  val deleteByIdPricelist: Command[BillingPricelistId] =
    sql"""
      DELETE FROM billing.pricelist
      WHERE id = $id
    """.command
}
