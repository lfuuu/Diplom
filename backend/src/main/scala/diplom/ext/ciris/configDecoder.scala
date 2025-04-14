package com.mcn.diplom.ext.ciris

import _root_.ciris.ConfigDecoder
import com.mcn.diplom.ext.derevo.Derive

object configDecoder extends Derive[Decoder.Id]

object Decoder {
  type Id[A] = ConfigDecoder[String, A]
}
