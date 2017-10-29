package com.dimsuz.yamm.data.sources.network.services

import com.dimsuz.yamm.data.sources.network.mappers.toDomainModel
import com.dimsuz.yamm.domain.models.ServerConfig
import com.dimsuz.yamm.domain.models.Team
import io.reactivex.Single
import io.reactivex.SingleTransformer

internal class MattermostService internal constructor(private val serviceApi: MattermostServiceApi) {
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

  fun getUserTeams(userId: String): Single<List<Team>> {
    return serviceApi
      .getUserTeams(userId).map { it.map { it.toDomainModel() } }
      .compose(sessionExpirationGuard.get())
  }
}