package com.dimsuz.yamm.util

import android.os.Looper

fun checkMainThread() {
  check(Looper.getMainLooper() == Looper.myLooper(),
    { "Expected to be called on main thread but was ${Thread.currentThread().name}" })
}