package com.dimsuz.yamm

import android.content.Context
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.PrefsBasedAppConfig
import toothpick.config.Module

internal class ApplicationModule(application: YammApplication) : Module() {
  init {
    bind(Context::class.java).toInstance(application)
    // not making it singleton, will be recreated each time it's needed.
    // Currently I think it will be rarely needed, but if this will become wrong, rethink
    bind(AppConfig::class.java).to(PrefsBasedAppConfig::class.java)
  }
}