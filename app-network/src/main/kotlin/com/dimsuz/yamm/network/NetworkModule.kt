package com.dimsuz.yamm.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import toothpick.config.Module
import java.util.concurrent.TimeUnit

private const val DEFAULT_CONNECT_TIMEOUT = 90000L

class NetworkModule(config: Config) : Module() {
  data class Config(
    val debugMode: Boolean,
    val logger: ((String) -> Unit)?
  )

  init {
    bind(OkHttpClient::class.java).toInstance(createHttpClient(config))
  }
}

private fun createHttpClient(config: NetworkModule.Config): OkHttpClient {
  return OkHttpClient.Builder()
    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .addNetworkInterceptor(createLoggingInterceptor(config))
    .build()
}

private fun createLoggingInterceptor(config: NetworkModule.Config): HttpLoggingInterceptor {
  val logLevel = if (config.debugMode) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
  val interceptor = if (config.logger != null) HttpLoggingInterceptor(config.logger) else HttpLoggingInterceptor()
  interceptor.level = logLevel
  return interceptor
}
