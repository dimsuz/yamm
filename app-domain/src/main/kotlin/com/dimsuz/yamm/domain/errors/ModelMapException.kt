package com.dimsuz.yamm.domain.errors

class ModelMapException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

fun throwExpectedNotNull(modelName: String, fieldName: String): Nothing {
  throw ModelMapException("expected $modelName.$fieldName not to be null")
}
