package com.dimsuz.yamm.network

import io.reactivex.SingleTransformer

interface SessionExpirationGuardProvider {
  fun <T> get(): SingleTransformer<T, T>
}