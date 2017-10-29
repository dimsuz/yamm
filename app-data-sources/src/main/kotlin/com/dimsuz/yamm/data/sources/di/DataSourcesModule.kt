package com.dimsuz.yamm.data.sources.di

import com.dimsuz.yamm.data.BuildConfig
import com.dimsuz.yamm.data.repositories.ServerConfigRepository
import com.dimsuz.yamm.data.sources.network.services.MattermostPublicApi
import com.dimsuz.yamm.data.sources.network.services.MattermostService
import com.dimsuz.yamm.data.sources.network.session.DefaultSessionManager
import com.dimsuz.yamm.data.sources.network.session.SessionManager
import com.dimsuz.yamm.data.sources.settings.PreferencesSettingsStorage
import com.dimsuz.yamm.data.sources.settings.SettingsStorage
import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import toothpick.config.Module
import java.util.concurrent.TimeUnit

private const val DEFAULT_CONNECT_TIMEOUT = 90000L
private const val API_VERSION = "v4"

class DataSourcesModule(serverUrl: String) : Module() {

  init {

    bind(SettingsStorage::class.java).to(PreferencesSettingsStorage::class.java)
    bind(SessionManager::class.java).to(DefaultSessionManager::class.java).singletonInScope()

    val moshi = createMoshi()
    bind(MattermostService::class.java).toInstance(createMattermostService(createHttpClient(), moshi, serverUrl))
    // expecting to use this rarely, so should be GCed after use...
    bind(MattermostPublicApi::class.java).toProviderInstance({
      createMattermostApi(createHttpClient(), moshi, serverUrl)
    })

    bind(ServerConfigRepository::class.java)
  }
}

private fun createHttpClient(): OkHttpClient {
  return OkHttpClient.Builder()
    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .addNetworkInterceptor(createLoggingInterceptor())
    .build()
}

private fun createLoggingInterceptor(): HttpLoggingInterceptor {
  val logLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
  val logger = { s: String -> Timber.tag("YammNetwork"); Timber.d(s) }
  val interceptor = HttpLoggingInterceptor(logger)
  interceptor.level = logLevel
  return interceptor
}

private fun createMoshi(): Moshi {
  return Moshi.Builder()
    .build()
}

private inline fun <reified T> createMattermostApi(httpClient: OkHttpClient, moshi: Moshi, serverUrl: String): T {
  val callAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
  val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(callAdapterFactory)
    .client(httpClient)
    .baseUrl("$serverUrl/api/$API_VERSION/")
    .build()
  return retrofit.create(T::class.java)
}

private fun createMattermostService(httpClient: OkHttpClient, moshi: Moshi, serverUrl: String): MattermostService {
  return MattermostService(createMattermostApi(httpClient, moshi, serverUrl))
}
