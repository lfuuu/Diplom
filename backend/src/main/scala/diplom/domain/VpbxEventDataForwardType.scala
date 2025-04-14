package com.mcn.diplom.domain

// https://gitlab.mcnloc.ru/cebolla/sorms/-/blob/master/modules/domain/shared/src/main/scala/sorm/domain/EventQueueModel.scala?ref_type=heads

import com.mcn.diplom.domain.DvoEventOutcoming.DvoEventOutcomingServiceCode
import io.circe.{ Decoder, Encoder, Json }
import sttp.tapir._

object VpbxEventDataForwardType {

  implicit def VpbxEventDataForwardSchema: Schema[VpbxEventDataForwardType] =
    Schema(SchemaType.SString())
      .validate(
        Validator
          .enumeration(List(UncondForwardType, CondForwardType, NoAnswerForwardType, BusyForwardType, UnavailForwardType), (c => Some(c.label)))
      )

  sealed abstract class VpbxEventDataForwardType(val label: String) {

    def toDvoEventOutcomingServiceCode: DvoEventOutcomingServiceCode =
      label match {
        case "uncond"   => DvoEventOutcomingServiceCode("CFU")
        case "cond"     => DvoEventOutcomingServiceCode("ALLCF")
        case "noanswer" => DvoEventOutcomingServiceCode("CFNRY")
        case "busy"     => DvoEventOutcomingServiceCode("CFB")
        case "unavail"  => DvoEventOutcomingServiceCode("ALLCF")
        case _          => DvoEventOutcomingServiceCode("UNKNOW")
      }
  }
  //case class Unknow(badLabel: String) extends VpbxEventDataForwardType("uknow")
  case object UncondForwardType   extends VpbxEventDataForwardType("uncond")
  case object CondForwardType     extends VpbxEventDataForwardType("cond")
  case object NoAnswerForwardType extends VpbxEventDataForwardType("noanswer")
  case object BusyForwardType     extends VpbxEventDataForwardType("busy")
  case object UnavailForwardType  extends VpbxEventDataForwardType("unavail")

  object VpbxEventDataForwardType {

    implicit val jsonEncoder: Encoder[VpbxEventDataForwardType] = new Encoder[VpbxEventDataForwardType] {

      final def apply(a: VpbxEventDataForwardType): Json = Json.fromString(a.label)
    }

    implicit val jsonDecoder: Decoder[VpbxEventDataForwardType] = Decoder[String].emap {
      case UncondForwardType.label   => Right(UncondForwardType)
      case CondForwardType.label     => Right(CondForwardType)
      case NoAnswerForwardType.label => Right(NoAnswerForwardType)
      case BusyForwardType.label     => Right(BusyForwardType)
      case UnavailForwardType.label  => Right(UnavailForwardType)
      case other                     => Left(s"неожиданное значение [$other] для поля forwardType")
    }

  }
}
