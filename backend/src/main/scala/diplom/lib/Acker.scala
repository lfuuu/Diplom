package com.mcn.diplom.lib

trait Acker[F[_], A] {
  def ack(id: Consumer.MsgId): F[Unit]
  def ack(ids: Set[Consumer.MsgId]): F[Unit]
  def nack(id: Consumer.MsgId): F[Unit]
}
