package com.dimsuz.yamm.data.sources.di

import android.content.Context
import com.dimsuz.yamm.data.BuildConfig
import com.dimsuz.yamm.data.repositories.ServerConfigRepository
import com.dimsuz.yamm.data.sources.network.services.MattermostPublicApi
import com.dimsuz.yamm.data.sources.network.services.MattermostService
import com.dimsuz.yamm.data.sources.network.session.DefaultSessionManager
import com.dimsuz.yamm.data.sources.network.session.SessionManager
import com.dimsuz.yamm.data.sources.network.session.SessionTokenAddInterceptor
import com.dimsuz.yamm.data.sources.settings.PreferencesSettingsStorage
import com.dimsuz.yamm.data.sources.settings.SettingsStorage
import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
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

class DataSourcesModule(context: Context, serverUrl: String) : Module() {

  init {

    val moshi = createMoshi()
    val settingsStorage = PreferencesSettingsStorage(context)
    val sessionManager = DefaultSessionManager(settingsStorage)

    bind(SettingsStorage::class.java).toInstance(settingsStorage)
    bind(SessionManager::class.java).toInstance(sessionManager)

    bind(MattermostService::class.java).toInstance(createMattermostService(moshi, serverUrl, sessionManager))
    // expecting to use this rarely, so should be GCed after use...
    bind(MattermostPublicApi::class.java).toProviderInstance({
      createMattermostApi(createHttpClient(), moshi, serverUrl)
    })

    bind(ServerConfigRepository::class.java)
  }
}

private fun createHttpClient(interceptors: List<Interceptor> = emptyList()): OkHttpClient {
  val builder = OkHttpClient.Builder()
    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)

  // add logging interceptor last, so it would see everything added by a previous ones
  interceptors.plus(createLoggingInterceptor()).forEach { builder.addNetworkInterceptor(it) }
  return builder.build()
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

private fun createMattermostService(moshi: Moshi, serverUrl: String, sessionManager: SessionManager): MattermostService {
  val httpClient = createHttpClient(listOf(SessionTokenAddInterceptor(sessionManager)))
  return MattermostService(createMattermostApi(httpClient, moshi, serverUrl))
}
