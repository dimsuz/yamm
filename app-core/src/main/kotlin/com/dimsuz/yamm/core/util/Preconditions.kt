package com.dimsuz.yamm.core.util

import android.os.Looper
import com.dimsuz.yamm.core.BuildConfig
import timber.log.Timber

fun checkMainThread() {
  check(Looper.getMainLooper() == Looper.myLooper(),
    { "Expected to be called on main thread but was ${Thread.currentThread().name}" })
}

fun checkWorkerThread() {
  check(Looper.getMainLooper() != Looper.myLooper(),
    { "Expected to be called on worker thread but was on the main thread" })
}

inline fun checkOrLog(value: Boolean, lazyMessage: () -> Any) {
  if (BuildConfig.DEBUG) {
    check(value, lazyMessage)
  } else {
    if (!value) {
      val message = lazyMessage()
      Timber.e(message.toString())
    }
  }
}
