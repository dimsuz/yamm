package com.dimsuz.yamm.data.sources.network.services

import com.dimsuz.yamm.data.sources.network.models.ServerConfigJson
import io.reactivex.Single
import retrofit2.http.GET

interface MattermostPublicApi {
  @GET("config/client?format=old")
  fun getServerConfig(): Single<ServerConfigJson>
}