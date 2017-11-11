package com.dimsuz.yamm.core.log

interface Logger {
  fun debug(message: String)
  fun error(message: String)
  fun error(throwable: Throwable, message: String)
}