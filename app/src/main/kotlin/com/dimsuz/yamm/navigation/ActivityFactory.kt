package com.dimsuz.yamm.navigation

import android.content.Context
import android.content.Intent

interface ActivityFactory {
  fun createIntent(context: Context, screenKey: String, payload: Any?): Intent
}