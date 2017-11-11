package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.ServerConfig
import io.reactivex.Observable

interface ServerConfigRepository {
  fun getServerConfig(): Observable<ServerConfig>
}