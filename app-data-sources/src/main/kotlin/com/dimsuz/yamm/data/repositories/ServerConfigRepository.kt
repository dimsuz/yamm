package com.dimsuz.yamm.data.repositories

import com.dimsuz.yamm.data.sources.network.services.MattermostService
import com.dimsuz.yamm.domain.models.ServerConfig
import io.reactivex.Observable
import javax.inject.Inject

class ServerConfigRepository @Inject internal constructor(private val mattermostService: MattermostService) {
  fun getServerConfig(): Observable<ServerConfig> {
    return mattermostService.getServerConfig().toObservable()
  }
}