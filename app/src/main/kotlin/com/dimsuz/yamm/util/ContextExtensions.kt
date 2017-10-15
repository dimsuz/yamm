package com.dimsuz.yamm.util

import android.content.Context
import toothpick.Toothpick

internal val Context.appScope get() = Toothpick.openScope(this.applicationContext)