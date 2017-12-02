package com.dimsuz.yamm.core.util

import kotlin.text.Typography.ellipsis

inline fun <T> Iterable<T>.forEachApply(action: T.() -> Unit) {
  for (e in this) {
    with(e) {
      action()
    }
  }
}

fun String.ellipsizeEnd(maxLength: Int): String {
  return if (length > maxLength) this.take(maxLength-1) + ellipsis else this
}