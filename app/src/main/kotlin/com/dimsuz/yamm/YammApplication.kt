package com.dimsuz.yamm

import android.app.Application
import com.dimsuz.yamm.data.sources.di.DataSourcesModule
import com.dimsuz.yamm.data.sources.network.session.SessionManager
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.instance
import ru.terrakok.cicerone.Cicerone
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick

class YammApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    configureLogging()
    configureAppScope()
  }

  private fun configureAppScope() {
    val appScope = Toothpick.openScope(this)
    // needs to go before createNetworkConfig() which already uses some bindings
    appScope.installModules(ApplicationModule(this, Cicerone.create()))
    val networkConfig = createNetworkConfig(appScope)
    if (networkConfig != null) {
      Timber.d("Configuring network module: $networkConfig")
      appScope.installModules(DataSourcesModule(networkConfig))
    } else {
      Timber.d("Network config is not available, skipping configuration for now")
    }
    configureAuthSession(appScope)
  }

  private fun configureAuthSession(appScope: Scope) {
    val sessionManager = appScope.instance<SessionManager>()
    sessionManager.initializeSession()
  }

  fun onServerUrlChanged() {
    // server url change implies that DataSourcesModule must be reconfigured.
    // Currently found no good way to simply re-add it, so must re-init whole app scope
    // which would recreate the DataSourcesModule with correct params along the way
    Toothpick.closeScope(this)
    configureAppScope()
  }

  private fun createNetworkConfig(appScope: Scope): DataSourcesModule.Config? {
    val serverUrl = appScope.getInstance(AppConfig::class.java)?.getServerUrl() ?: return null
    return DataSourcesModule.Config(serverUrl,
      debugMode = BuildConfig.DEBUG,
      logger = { Timber.tag("YammNetwork"); Timber.d(it) })
  }

  private fun configureLogging() {
    Timber.plant(Timber.DebugTree())
  }
}