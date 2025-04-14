package com.mcn.diplom.domain

import scala.util.control.NoStackTrace

import derevo.cats._
import derevo.derive

@derive(show)
case object EmptyCartError extends NoStackTrace

sealed trait UserError extends NoStackTrace

case class UserAlreadyExists(username: String) extends UserError {
  override def toString(): String = s"UserAlreadyExists(${username})"
}

case class UserNotFound(username: String) extends UserError {
  override def toString(): String = s"UserNotFound(${username})"
}

case class InvalidUserAge(age: Int) extends UserError {
  override def toString(): String = s"InvalidUserAge(${age})"
}
case class InvalidClaimContent()    extends UserError

case class FileAlreadyExistsError(msg: String) extends NoStackTrace {
  override def toString(): String = s"FileAlreadyExistsError(${msg})"
}

case class CfgVarIsNotDefinedError(msg: String) extends NoStackTrace {
  override def toString(): String = s"CfgVarIsNotDefinedError(${msg})"
}

case class FileNotFoundError(msg: String) extends NoStackTrace {
  override def toString(): String = s"FileNotFoundError(${msg})"
}

case class ErrorCallRestApi(error: String) extends NoStackTrace {
  override def toString(): String = s"ErrorCallRestApi(${error})"
}

case class DictionaryServiceNotFoundError(msg: String) extends NoStackTrace {
  override def toString(): String = s"DictionaryServiceNotFoundError(${msg})"
}

sealed trait RadiusError extends NoStackTrace

case class RadiusPacketDecodeError(msg: String) extends RadiusError {
  override def toString(): String = s"RadiusPacketDecodeError(${msg})"
}

case class RadiusPacketCodeError(msg: String) extends RadiusError {
  override def toString(): String = s"RadiusPacketCodeError(${msg})"
}
