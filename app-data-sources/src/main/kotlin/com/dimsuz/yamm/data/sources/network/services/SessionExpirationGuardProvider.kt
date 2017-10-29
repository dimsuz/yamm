package com.dimsuz.yamm.data.sources.network.services

import io.reactivex.SingleTransformer

internal interface SessionExpirationGuardProvider {
  fun <T> get(): SingleTransformer<T, T>
}