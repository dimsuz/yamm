package com.dimsuz.yamm.network

import com.dimsuz.yamm.domain.models.ServerConfig
import com.dimsuz.yamm.network.mappers.toDomainModel
import io.reactivex.Single
import io.reactivex.SingleTransformer

class MattermostService internal constructor(private val serviceApi: MattermostServiceApi) {
  var authToken: String? = null
  var sessionExpirationGuard: SessionExpirationGuardProvider = object : SessionExpirationGuardProvider {
    override fun <T> get(): SingleTransformer<T, T> {
      return SingleTransformer { s -> s }
    }
  }

  fun getServerConfig(): Single<ServerConfig> {
    return serviceApi
      .getServerConfig().map { it.toDomainModel() }
      .compose(sessionExpirationGuard.get())
  }
}