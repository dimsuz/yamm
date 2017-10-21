package com.dimsuz.yamm.util

class DefaultErrorDetailsExtractor : ErrorDetailsExtractor {
  override fun extractErrorText(error: Throwable): String {
    return error.message ?: "unknown error"
  }
}