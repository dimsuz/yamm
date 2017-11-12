package com.dimsuz.yamm.data.sources.network.services

import com.dimsuz.yamm.data.sources.network.models.ChannelJson
import com.dimsuz.yamm.data.sources.network.models.TeamJson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface MattermostAuthorizedApi {

  @GET("users/{user_id}/teams")
  fun getUserTeams(@Path("user_id") userId: String): Single<List<TeamJson>>

  @GET("users/{user_id}/teams/{team_id}/channels")
  fun getUserChannels(@Path("user_id") userId: String,
                      @Path("team_id") teamId: String): Single<List<ChannelJson>>
}