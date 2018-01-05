package com.dimsuz.yamm.data.sources.network.services

import com.dimsuz.yamm.data.sources.network.models.ChannelJson
import com.dimsuz.yamm.data.sources.network.models.PostCreateParams
import com.dimsuz.yamm.data.sources.network.models.PostListJson
import com.dimsuz.yamm.data.sources.network.models.TeamJson
import com.dimsuz.yamm.data.sources.network.models.UserJson
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MattermostAuthorizedApi {

  @GET("users/{user_id}/teams")
  fun getUserTeams(@Path("user_id") userId: String): Single<List<TeamJson>>

  @GET("users/{user_id}/teams/{team_id}/channels")
  fun getUserChannels(@Path("user_id") userId: String,
                      @Path("team_id") teamId: String): Single<List<ChannelJson>>

  @GET("channels/{channel_id}")
  fun getChannel(@Path("channel_id") channelId: String): Single<ChannelJson>

  @POST("users/ids")
  fun getUsersByIds(@Body ids: List<String>): Single<List<UserJson>>

  @GET("channels/{channel_id}/posts")
  fun getChannelPosts(@Path("channel_id") channelId: String,
                      @Query("page") page: Int?,
                      @Query("per_page") perPage: Int?,
                      @Query("since") sinceTimestamp: Int?,
                      @Query("before") beforePostId: String?,
                      @Query("after") afterPostId: String?): Single<PostListJson>

  @POST("posts")
  fun createPost(@Body params: PostCreateParams): Single<Any>
}