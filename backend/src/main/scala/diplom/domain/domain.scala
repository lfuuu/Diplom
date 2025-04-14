package com.mcn.diplom.domain

import java.time.format.DateTimeFormatter
import java.time.{ Instant, LocalDate, ZoneId }
import java.util.UUID

import scala.util.Try

import cats.Show
import cats.kernel.Eq
import cats.syntax.all._
import com.comcast.ip4s.Ipv4Address
import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.circe.{ Decoder, Encoder }
import io.estatico.newtype.macros.newtype
import org.http4s.{ ParseFailure, QueryParamDecoder }
import sttp.tapir.{ Schema, SchemaType, Validator }

object misc {

  @derive(decoder, encoder, eqv, show)
  @newtype
  case class Datestamp(value: LocalDate)

  implicit val encodeLocalDate: Encoder[LocalDate] = Encoder.encodeString.contramap[LocalDate](_.toString)
  implicit val decodeLocalDate: Decoder[LocalDate] = Decoder.decodeString.emapTry(str => Try(LocalDate.parse(str)))

  implicit val eqLocalDate: Eq[LocalDate] = Eq.instance[LocalDate] { (intstanseA, intstanseB) =>
    intstanseA.equals(intstanseB)
  }

  implicit val showLocalDate: Show[LocalDate] = Show.show(_.toString)

  @derive(decoder, encoder, eqv)
  @newtype
  case class Timestamp(value: Instant)

  val formatedDateTime = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneId.systemDefault())

  implicit def timestampSchema: Schema[Timestamp] =
    Schema(SchemaType.SDateTime())

  implicit def instantSchema: Schema[Instant] =
    Schema(SchemaType.SDateTime())

  implicit val showTimestamp: Show[Timestamp] = Show.show(x => formatedDateTime.format(x.value))

  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emapTry(str => Try(Instant.parse(str)))

  implicit val eqInstant: Eq[Instant] = Eq.instance[Instant] { (intstanseA, intstanseB) =>
    intstanseA.equals(intstanseB)
  }

  implicit val showInstant: Show[Instant] = Show.show(_.toString)

  implicit val queryParamDecoder: QueryParamDecoder[UUID] = QueryParamDecoder[String].emap { x =>
    Try(UUID.fromString(x)).toEither.leftMap(e => ParseFailure(e.getMessage(), e.getMessage()))
  }

  implicit def ipv4AddressSchema: Schema[Ipv4Address] = Schema(SchemaType.SString()).validate(Validator.all())

  implicit val ipv4AddressEncoder: Encoder[Ipv4Address] =
    Encoder[String].contramap(_.toUriString)

  implicit val ipv4AddressDecoder: Decoder[Ipv4Address] =
    Decoder.decodeString.emap(addr =>
      Ipv4Address.fromString(addr) match {
        case None        => Left(s"Неправильный адрес ${addr}")
        case Some(value) => Right(value)
      }
    )

}
