package com.dimsuz.yamm.core.util

import android.os.Looper

fun checkMainThread() {
  check(Looper.getMainLooper() == Looper.myLooper(),
    { "Expected to be called on main thread but was ${Thread.currentThread().name}" })
}

fun checkWorkerThread() {
  check(Looper.getMainLooper() != Looper.myLooper(),
    { "Expected to be called on worker thread but was on the main thread" })
}
