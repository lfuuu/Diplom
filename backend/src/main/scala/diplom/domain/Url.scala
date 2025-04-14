package com.mcn.diplom.domain

final case class Url(value: String) extends AnyVal {
  override def toString: String = value
}

import scala.util.Try

final case class Urls(values: Vector[Url]) extends AnyVal {

  def currentOpt: Option[Url] =
    Try(currentUnsafe).toOption

  def currentUnsafe: Url =
    values.head

  def remove(url: Url): Urls =
    copy(values.filter(_ != url))

  def add(url: Url): Urls =
    if (values contains url) this
    else copy(values :+ url)

  def next: Urls =
    Try(Urls(this.values.tail :+ this.values.head))
      .getOrElse(Urls.empty)

}

object Urls {
  def empty: Urls = Urls(Vector.empty)
}
