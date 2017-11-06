package com.dimsuz.yamm.data.sources.network.services

import com.dimsuz.yamm.data.sources.network.models.TeamJson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MattermostAuthorizedApi {

  @GET("users/{user_id}/teams")
  fun getUserTeams(@Path("user_id") userId: String): Single<List<TeamJson>>
}