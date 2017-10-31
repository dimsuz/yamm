package com.dimsuz.yamm.navigation

internal inline fun <reified T> payloadAsParam(payload: Any?, paramName: String): T {
  require(payload != null) { "expected $paramName, got null"}
  return payload as? T ?: throw IllegalArgumentException("expected $paramName as ${T::class.java.simpleName}, got $payload")
}
