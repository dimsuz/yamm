package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.services.MattermostPublicApi
import com.dimsuz.yamm.domain.models.ServerConfig
import com.dimsuz.yamm.domain.repositories.ServerConfigRepository
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Observable
import javax.inject.Inject

internal class ServerConfigRepositoryImpl @Inject internal constructor(private val publicApi: MattermostPublicApi) : ServerConfigRepository {
  override fun getServerConfig(): Observable<ServerConfig> {
    return publicApi.getServerConfig()
      .map { it.toDomainModel() }
      .toObservable()
  }
}