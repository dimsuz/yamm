package com.dimsuz.yamm

import android.content.Context
import com.dimsuz.yamm.util.*
import toothpick.config.Module

internal class ApplicationModule(application: YammApplication) : Module() {
  init {
    bind(Context::class.java).toInstance(application)
    bind(YammApplication::class.java).toInstance(application)
    bind(AppSchedulers::class.java).toInstance(DefaultAppSchedulers)
    bind(ErrorDetailsExtractor::class.java).toInstance(DefaultErrorDetailsExtractor())
    // not making it singleton, will be recreated each time it's needed.
    // Currently I think it will be rarely needed, but if this will become wrong, rethink
    bind(AppConfig::class.java).to(PrefsBasedAppConfig::class.java)
  }
}