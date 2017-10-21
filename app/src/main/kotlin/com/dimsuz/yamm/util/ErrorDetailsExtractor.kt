package com.dimsuz.yamm.util

interface ErrorDetailsExtractor {
  fun extractErrorText(error: Throwable): String
}