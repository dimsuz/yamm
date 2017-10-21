package com.dimsuz.yamm.network

import com.dimsuz.yamm.network.models.ServerConfigJson
import io.reactivex.Single
import retrofit2.http.GET

internal interface MattermostServiceApi {

  @GET("config/client?format=old")
  fun getServerConfig(): Single<ServerConfigJson>

}