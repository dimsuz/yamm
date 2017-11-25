package com.dimsuz.yamm.util

import com.dimsuz.yamm.core.log.Logger
import timber.log.Timber

class TimberLogger : Logger {
  override fun debug(message: String) {
    Timber.d(message)
  }

  override fun error(message: String) {
    Timber.e(message)
  }

  override fun error(throwable: Throwable, message: String) {
    Timber.e(throwable, message)
  }

  override fun checkMainThread() {
    com.dimsuz.yamm.util.checkMainThread()
  }
}