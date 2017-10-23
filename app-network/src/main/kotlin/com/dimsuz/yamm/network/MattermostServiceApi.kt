package com.dimsuz.yamm.network

import com.dimsuz.yamm.network.models.ServerConfigJson
import com.dimsuz.yamm.network.models.TeamJson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MattermostServiceApi {

  @GET("config/client?format=old")
  fun getServerConfig(): Single<ServerConfigJson>

  @GET("users/{user_id}/teams")
  fun getUserTeams(@Path("user_id") userId: String): Single<List<TeamJson>>
}