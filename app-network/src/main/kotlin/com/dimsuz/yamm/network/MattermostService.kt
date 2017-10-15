package com.dimsuz.yamm.network

import com.dimsuz.yamm.domain.ServerConfig
import com.dimsuz.yamm.network.mappers.toDomainModel

class MattermostService internal constructor(private val serviceApi: MattermostServiceApi) {
  fun getServerConfig(): ServerConfig {
    return serviceApi.getServerConfig().toDomainModel()
  }
}