package com.dimsuz.yamm.data.sources.network.session

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

private const val TOKEN_HEADER = "Authorization"

internal class SessionTokenAddInterceptor (private val sessionManager: SessionManager): Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var request = chain.request()

    if (chain.request().header(TOKEN_HEADER) == null) {
      val token = sessionManager.currentSessionToken
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