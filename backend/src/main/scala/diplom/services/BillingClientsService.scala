package com.mcn.diplom.services

import java.time.ZoneOffset

import cats.effect._
import cats.syntax.all._
import com.mcn.diplom.domain.nispd.BillingClient._
import skunk._
import skunk.codec.all._
import skunk.implicits._

trait BillingClientsService[F[_]] {
  def create(client: BillingClientCreateRequest): F[Option[BillingClientId]]
  def findAll: F[List[BillingClient]]
  def findById(id: BillingClientId): F[Option[BillingClient]]
  def deleteById(id: BillingClientId): F[Unit]
}

object BillingClientsService {

  def make[F[_]: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): BillingClientsService[F] =
    new BillingClientsService[F] {
      import BillingClientsSQL._

      def findAll: F[List[BillingClient]] =
        postgres.use(_.execute(selectAll))

      def create(client: BillingClientCreateRequest): F[Option[BillingClientId]] =
        postgres.use { session =>
          session.prepare(insertBillingClient).flatMap(_.option(client))
        }

      def findById(id: BillingClientId): F[Option[BillingClient]] =
        postgres.use { session =>
          session.prepare(findByIdBillingClient).flatMap(_.option(id))
        }

      def deleteById(id: BillingClientId): F[Unit] =
        postgres.use { session =>
          session.execute(deleteByIdBillingClient)(id).void
        }

    }
}

private object BillingClientsSQL {

  val id: Codec[BillingClientId]               = int4.imap(BillingClientId(_))(_.value)
  val dtCreate: Codec[BillingClientDt]         = timestamptz.imap(t => BillingClientDt(t.toInstant))(_.value.atOffset(ZoneOffset.UTC))
  val balance: Codec[BillingClientBalance]     = numeric(12, 2).imap(BillingClientBalance(_))(_.value)
  val isBlocked: Codec[BillingClientIsBlocked] = bool.imap(BillingClientIsBlocked(_))(_.value)
  val name: Codec[BillingClientName]           = text.imap(BillingClientName(_))(_.value)

  val findAllCodec       = id *: dtCreate *: balance *: isBlocked *: name
  val createRequestCodec = balance *: isBlocked *: name

  val selectAll: Query[Void, BillingClient] =
    sql"""
        SELECT id, dt_create, balance, is_blocked, name FROM billing.clients        
       """.query(findAllCodec).to[BillingClient]

  val insertBillingClient: Query[BillingClientCreateRequest, BillingClientId] =
    sql"""
      INSERT INTO billing.clients(balance, is_blocked, name) VALUES ($createRequestCodec) RETURNING id;
      """
      .query(int4)
      .contramap[BillingClientCreateRequest] { c =>
        c.balance *: c.isBlocked *: c.name *: EmptyTuple
      }
      .map { case f => BillingClientId(f) }

  val findByIdBillingClient: Query[BillingClientId, BillingClient]            =
    sql"""
        SELECT id, dt_create, balance, is_blocked, name FROM billing.clients  where id = $id      
       """.query(findAllCodec).to[BillingClient]

  val deleteByIdBillingClient: Command[BillingClientId] =
    sql"""
        DELETE from billing.clients  where id = $id      
       """.command

}
