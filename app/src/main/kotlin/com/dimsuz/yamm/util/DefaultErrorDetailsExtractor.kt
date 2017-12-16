package com.dimsuz.yamm.util

import timber.log.Timber

class DefaultErrorDetailsExtractor : ErrorDetailsExtractor {
  override fun extractErrorText(error: Throwable): String {
    Timber.e(error)
    return error.message ?: "unknown error"
  }
}