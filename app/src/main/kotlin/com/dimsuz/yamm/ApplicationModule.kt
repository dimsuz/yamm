package com.dimsuz.yamm

import android.content.Context
import com.dimsuz.yamm.session.DefaultSessionManager
import com.dimsuz.yamm.session.SessionManager
import com.dimsuz.yamm.settings.PreferencesSettingsStorage
import com.dimsuz.yamm.settings.SettingsStorage
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
    bind(SettingsStorage::class.java).to(PreferencesSettingsStorage::class.java)
    bind(SessionManager::class.java).to(DefaultSessionManager::class.java).singletonInScope()
  }
}