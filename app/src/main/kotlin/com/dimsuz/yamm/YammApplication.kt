package com.dimsuz.yamm

import android.app.Application
import com.dimsuz.yamm.domain.di.DomainModule
import com.dimsuz.yamm.domain.util.AppConfig
import com.dimsuz.yamm.repositories.di.RepositoriesCommonModule
import com.dimsuz.yamm.repositories.di.RepositoriesModule
import com.dimsuz.yamm.util.instance
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import ru.terrakok.cicerone.Cicerone
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick

class YammApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    configureLogging()
    val appScope = configureAppScope()
    configurePicasso(appScope)
  }

  private fun configureAppScope(): Scope {
    val appScope = Toothpick.openScope(this)
    appScope.installModules(
      ApplicationModule(this, Cicerone.create()),
      RepositoriesCommonModule())
    configureUrlDependentDataSources(appScope)
    return configureUrlDependentDataSources(appScope)
  }

  private fun configurePicasso(appScope: Scope) {
    // Picasso can be used only once we have a fully configured OkHttpClient
    if (appScope.name == FULL_APP_SCOPE) {
      val picasso = Picasso.Builder(this)
        .downloader(OkHttp3Downloader(appScope.instance<OkHttpClient>()))
        .build()
      Picasso.setSingletonInstance(picasso)
    } else {
      Timber.d("skipping Picasso configuration, due to not configured full app scope")
    }
  }


  fun onServerUrlChanged() {
    // server url change implies that DataSourcesModule must be reconfigured.
    // using technique described in this post:
    // https://stackoverflow.com/questions/46760226/is-there-a-way-to-re-add-a-module-to-a-scope-in-a-toothpick-di-library
    Toothpick.closeScope(FULL_APP_SCOPE)
    val appScope = configureUrlDependentDataSources(Toothpick.openScope(this))
    // also reconfigure other services depending on new scope
    configurePicasso(appScope)
  }

  private fun configureUrlDependentDataSources(scope: Scope): Scope {
    val serverUrl = scope.instance<AppConfig>().getServerUrl()
    return if (serverUrl != null) {
      Timber.d("Configuring data sources module: $serverUrl")
      Toothpick.openScopes(this, FULL_APP_SCOPE).apply {
        installModules(
          RepositoriesModule(serverUrl),
          DomainModule(),
          AuthorizedApplicationModule()
        )
      }
    } else {
      Timber.d("Network config is not available, skipping configuration for now")
      Toothpick.closeScope(FULL_APP_SCOPE)
      scope
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
