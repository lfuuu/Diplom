package com.mcn.diplom.lib

import cats.effect.kernel._
import io.circe.{ Json, JsonObject }

trait JsonUtils[F[_]] {
  def addPrefixToKey(js: Json, prefix: String): F[Json]
  def deepMergeWithKeyPrefix(jSrc: Json, jAdd: Json, prefix: String): F[Json]
  def addStringVal(jSrc: Json, jo: Json): F[Json]
}

object JsonUtils {

  private def impAddPrefixToKey(js: Json, prefix: String): Json =
    js.asObject match {
      case Some(lhs) =>
        Json.fromJsonObject(
          lhs.toIterable.foldLeft(JsonObject.empty) {
            case (acc, (key, value)) =>
              acc.add(prefix + key, value)
          }
        )
      case _         => js
    }

  def apply[F[_]: JsonUtils]: JsonUtils[F] = implicitly

  implicit def forSync[F[_]: Sync]: JsonUtils[F] =
    new JsonUtils[F] {

      def addPrefixToKey(js: Json, prefix: String): F[Json] = Sync[F].delay(impAddPrefixToKey(js, prefix))

      def deepMergeWithKeyPrefix(jSrc: Json, jAdd: Json, prefix: String) =
        Sync[F].delay(jSrc.deepMerge(impAddPrefixToKey(jAdd, prefix)))

      def addStringVal(jSrc: Json, jo: Json): F[Json] =
        Sync[F].delay(jSrc.deepMerge(jo))

    }

}
