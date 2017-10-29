package com.dimsuz.yamm.data.sources.di

import com.dimsuz.yamm.data.repositories.ServerConfigRepository
import com.dimsuz.yamm.data.sources.network.services.MattermostService
import com.dimsuz.yamm.data.sources.network.services.MattermostServiceApi
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
import toothpick.config.Module
import java.util.concurrent.TimeUnit

private const val DEFAULT_CONNECT_TIMEOUT = 90000L
private const val API_VERSION = "v4"

class DataSourcesModule(config: Config) : Module() {
  data class Config(
    val serverUrl: String,
    val debugMode: Boolean,
    val logger: ((String) -> Unit)?
  )

  init {
    val httpClient = createHttpClient(config)
    val moshi = createMoshi()

    bind(SettingsStorage::class.java).to(PreferencesSettingsStorage::class.java)
    bind(SessionManager::class.java).to(DefaultSessionManager::class.java).singletonInScope()
    bind(MattermostService::class.java).toInstance(createMattermostService(httpClient, moshi, config))
    bind(ServerConfigRepository::class.java)
  }
}

private fun createHttpClient(config: DataSourcesModule.Config): OkHttpClient {
  return OkHttpClient.Builder()
    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .addNetworkInterceptor(createLoggingInterceptor(config))
    .build()
}

private fun createLoggingInterceptor(config: DataSourcesModule.Config): HttpLoggingInterceptor {
  val logLevel = if (config.debugMode) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
  val interceptor = if (config.logger != null) HttpLoggingInterceptor(config.logger) else HttpLoggingInterceptor()
  interceptor.level = logLevel
  return interceptor
}

private fun createMoshi(): Moshi {
  return Moshi.Builder()
    .build()
}

private fun createMattermostService(httpClient: OkHttpClient, moshi: Moshi, config: DataSourcesModule.Config): MattermostService {
  val callAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
  val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(callAdapterFactory)
    .client(httpClient)
    .baseUrl("${config.serverUrl}/api/$API_VERSION/")
    .build()
  return MattermostService(retrofit.create(MattermostServiceApi::class.java))
}