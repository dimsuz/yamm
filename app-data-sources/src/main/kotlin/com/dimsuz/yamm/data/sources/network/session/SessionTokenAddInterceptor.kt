package com.dimsuz.yamm.data.sources.network.session

import com.dimsuz.yamm.data.sources.network.models.AuthSession
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

private const val TOKEN_HEADER = "Authorization"

internal class SessionTokenAddInterceptor @Inject constructor(private val sessionProvider: Provider<AuthSession?>): Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()

    if (chain.request().header(TOKEN_HEADER) == null) {
      val token = sessionProvider.get()?.token
      if (token != null) {
        request = request.newBuilder()
          .addHeader(TOKEN_HEADER, token)
          .build()
      } else {
        Timber.e("not adding auth token, none found!")
      }
    }

    return chain.proceed(request)
  }
}