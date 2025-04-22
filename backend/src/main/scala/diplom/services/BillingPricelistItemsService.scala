package com.mcn.diplom.services

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import _root_.com.mcn.diplom.domain.nispd.BillingPricelistItem._

trait BillingPricelistItemsService[F[_]] {
  def create(item: BillingPricelistItemCreateRequest): F[Option[BillingPricelistItemId]]
  def findAll: F[List[BillingPricelistItem]]
  def findById(id: BillingPricelistItemId): F[Option[BillingPricelistItem]]
  def deleteById(id: BillingPricelistItemId): F[Unit]
}

object BillingPricelistItemsService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): BillingPricelistItemsService[F] =
    new BillingPricelistItemsService[F] {
      import BillingPricelistItemsSQL._

      override def findAll: F[List[BillingPricelistItem]] =
        postgres.use(_.execute(selectAll))

      override def create(request: BillingPricelistItemCreateRequest): F[Option[BillingPricelistItemId]] =
        postgres.use { session =>
          session.prepare(insertPricelistItem).flatMap(_.option(request))
        }

      override def findById(id: BillingPricelistItemId): F[Option[BillingPricelistItem]] =
        postgres.use { session =>
          session.prepare(findByIdPricelistItem).flatMap(_.option(id))
        }

      override def deleteById(id: BillingPricelistItemId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteByIdPricelistItem)(id).void
        }
    }
}

private object BillingPricelistItemsSQL {

  val id: Codec[BillingPricelistItemId]         = int4.imap(BillingPricelistItemId(_))(_.value)
  val pricelistId: Codec[BillingPricelistId]    = int4.imap(BillingPricelistId(_))(_.value)
  val ndef: Codec[BillingPriceNdefId]           = int8.imap(BillingPriceNdefId(_))(_.value)
  val dateFrom: Codec[BillingPriceDateFrom]     = date.imap(BillingPriceDateFrom(_))(_.value)
  val dateTo: Codec[Option[BillingPriceDateTo]] = date.opt.imap(_.map(BillingPriceDateTo(_)))(_.map(_.value))
  val price: Codec[BillingPrice]                = numeric(8, 4).imap(BillingPrice(_))(_.value)

  val findAllCodec       = id *: pricelistId *: ndef *: dateFrom *: dateTo *: price
  val createRequestCodec = pricelistId *: ndef *: dateFrom *: dateTo *: price

  val selectAll: Query[Void, BillingPricelistItem] =
    sql"""
      SELECT id, pricelist_id, ndef, date_from, date_to, price
      FROM billing.pricelist_item
    """.query(findAllCodec).to[BillingPricelistItem]

  val insertPricelistItem: Query[BillingPricelistItemCreateRequest, BillingPricelistItemId] =
    sql"""
      INSERT INTO billing.pricelist_item 
        (pricelist_id, ndef, date_from, date_to, price)
      VALUES ($pricelistId, $ndef, $dateFrom, $dateTo, $price)
      RETURNING id
    """
      .query(int4)
      .contramap[BillingPricelistItemCreateRequest] { req =>
        req.pricelistId *: req.ndef *: req.dateFrom *: req.dateTo *: req.price *: EmptyTuple
      }
      .map(BillingPricelistItemId(_))

  val findByIdPricelistItem: Query[BillingPricelistItemId, BillingPricelistItem] =
    sql"""
      SELECT id, pricelist_id, ndef, date_from, date_to, price
      FROM billing.pricelist_item
      WHERE id = $id
    """.query(findAllCodec).to[BillingPricelistItem]

  val deleteByIdPricelistItem: Command[BillingPricelistItemId] =
    sql"""
      DELETE FROM billing.pricelist_item
      WHERE id = $id
    """.command
}
