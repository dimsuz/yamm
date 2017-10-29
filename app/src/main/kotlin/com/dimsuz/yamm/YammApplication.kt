package com.dimsuz.yamm

import android.app.Application
import com.dimsuz.yamm.data.sources.di.DataSourcesModule
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
    appScope.installModules(ApplicationModule(this, Cicerone.create()))
    configureDataSources(appScope)
  }

  fun onServerUrlChanged() {
    // server url change implies that DataSourcesModule must be reconfigured.
    // using technique described in this post:
    // https://stackoverflow.com/questions/46760226/is-there-a-way-to-re-add-a-module-to-a-scope-in-a-toothpick-di-library
    Toothpick.closeScope(FULL_APP_SCOPE)
    configureDataSources(Toothpick.openScope(this))
  }

  private fun configureDataSources(scope: Scope) {
    val serverUrl = scope.instance<AppConfig>().getServerUrl()
    if (serverUrl != null) {
      Timber.d("Configuring network module: $serverUrl")
      val newScope = Toothpick.openScopes(this, FULL_APP_SCOPE)
      newScope.installModules(DataSourcesModule(serverUrl))
    } else {
      Timber.d("Network config is not available, skipping configuration for now")
      Toothpick.closeScope(FULL_APP_SCOPE)
    }
  }

  private fun configureLogging() {
    Timber.plant(Timber.DebugTree())
  }
}

@javax.inject.Scope
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class WithDataSources

@Suppress("PropertyName")
internal inline val FULL_APP_SCOPE get() = WithDataSources::class.java
