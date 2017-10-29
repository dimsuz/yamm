package com.dimsuz.yamm.util

import android.content.Context
import com.dimsuz.yamm.FULL_APP_SCOPE
import toothpick.Scope
import toothpick.Toothpick

internal inline val Context.appScope: Scope get() = Toothpick.openScopes(this.applicationContext, FULL_APP_SCOPE)