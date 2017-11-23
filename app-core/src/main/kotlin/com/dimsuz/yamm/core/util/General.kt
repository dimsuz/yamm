package com.dimsuz.yamm.core.util

inline fun <T> Iterable<T>.forEachApply(action: T.() -> Unit) {
  for (e in this) {
    with(e) {
      action()
    }
  }
}
