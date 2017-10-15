package com.dimsuz.yamm.network

import com.dimsuz.yamm.network.models.ServerConfigJson
import retrofit2.http.GET

internal interface MattermostServiceApi {

  @GET("config/client?format=old")
  fun getServerConfig(): ServerConfigJson

}