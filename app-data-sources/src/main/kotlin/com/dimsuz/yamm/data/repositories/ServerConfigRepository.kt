package com.dimsuz.yamm.data.repositories

import com.dimsuz.yamm.data.sources.network.mappers.toDomainModel
import com.dimsuz.yamm.data.sources.network.services.MattermostPublicApi
import com.dimsuz.yamm.domain.models.ServerConfig
import io.reactivex.Observable
import javax.inject.Inject

class ServerConfigRepository @Inject internal constructor(private val publicApi: MattermostPublicApi) {
  fun getServerConfig(): Observable<ServerConfig> {
    return publicApi.getServerConfig()
      .map { it.toDomainModel() }
      .toObservable()
  }
}