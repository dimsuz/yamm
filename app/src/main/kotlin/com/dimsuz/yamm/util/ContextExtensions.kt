package com.dimsuz.yamm.util

import android.content.Context
import toothpick.Toothpick

inline val Context.appScope get() = Toothpick.openScope(this.applicationContext)