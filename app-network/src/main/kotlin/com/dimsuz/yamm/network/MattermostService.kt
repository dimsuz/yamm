package com.dimsuz.yamm.network

import com.dimsuz.yamm.domain.ServerConfig
import com.dimsuz.yamm.network.mappers.toDomainModel
import io.reactivex.Single

class MattermostService internal constructor(private val serviceApi: MattermostServiceApi) {
  fun getServerConfig(): Single<ServerConfig> {
    return serviceApi.getServerConfig().map { it.toDomainModel() }
  }
}