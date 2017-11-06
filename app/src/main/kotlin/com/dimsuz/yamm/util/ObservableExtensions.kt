package com.dimsuz.yamm.util

import com.dimsuz.yamm.BuildConfig
import io.reactivex.Observable

/**
 * Performs `onNext` action only if current build type is DEBUG
 */
fun <T> Observable<T>.doOnNextDebug(lazyAction: (T) -> Unit): Observable<T> {
  return if (BuildConfig.DEBUG) this.doOnNext(lazyAction) else this
}