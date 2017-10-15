package com.dimsuz.yamm

import android.app.Application
import timber.log.Timber

class YammApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    configureLogging()
  }

  private fun configureLogging() {
    Timber.plant(Timber.DebugTree())
  }
}