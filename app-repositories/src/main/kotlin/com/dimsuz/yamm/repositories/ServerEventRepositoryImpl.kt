package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.models.WebSocketAuthJson
import com.dimsuz.yamm.data.sources.network.models.WebSocketMmEvent
import com.dimsuz.yamm.data.sources.network.services.MattermostEventsApi
import com.dimsuz.yamm.data.sources.network.services.WebSocketEvent
import com.dimsuz.yamm.domain.errors.throwExpectedNotNull
import com.dimsuz.yamm.domain.models.ServerEvent
import com.dimsuz.yamm.domain.repositories.PostRepository
import com.dimsuz.yamm.domain.repositories.ServerEventRepository
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.domain.repositories.UserRepository
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

internal class ServerEventRepositoryImpl @Inject constructor(
  private val mattermostEventsApi: MattermostEventsApi,
  private val sessionManager: SessionManager,
  private val userRepository: UserRepository,
  private val postRepository: PostRepository) : ServerEventRepository {

  override fun serverEventsLive(connectStateChanges: Observable<Boolean>): Observable<ServerEvent> {
    return connectStateChanges.distinctUntilChanged()
      .switchMap { isConnectRequest ->
        if (isConnectRequest) {
          mattermostEventsApi.createNewEventStream()
            .doOnNext { event ->
              if (event is WebSocketEvent.Open) {
                mattermostEventsApi.sendMessage(WebSocketAuthJson(sessionManager.currentSessionToken!!))
              }
            }
            .ofType(WebSocketEvent.TextMessage::class.java)
            .flatMapMaybe { extractServerEvent(it) }
        } else {
          Completable
            .fromAction { mattermostEventsApi.closeConnection() }
            .andThen(Observable.never<ServerEvent>())
        }
      }
  }

  private fun extractServerEvent(message: WebSocketEvent.TextMessage): Maybe<ServerEvent> {
    val event = message.event
    return if (event?.data != null) {
      createEventFromData(event.data!!)
    } else {
      // ??? there might be some events without data?...
      Maybe.just(ServerEvent.Unknown)
    }
  }

  private fun createEventFromData(data: WebSocketMmEvent.Data): Maybe<ServerEvent> {
    return when (data) {
      is WebSocketMmEvent.Data.Posted -> {
        val postNM = data.post ?: throwExpectedNotNull("posted.data", "post")
        val userId = postNM.user_id ?: throwExpectedNotNull("posted.data.post", "user_id")
        val channelId = postNM.channel_id ?: throwExpectedNotNull("posted.data.post", "channel_id")
        val channelName = data.channel_name ?: throwExpectedNotNull("posted.data", "channel_name")
        val teamId = data.team_id ?: throwExpectedNotNull("posted.data", "team_id")
        userRepository.getUser(userId)
          .map { user ->
            val post = postNM.toDomainModel(user)
            postRepository.insert(post)
            ServerEvent.Posted(channelId, teamId, channelName, post)
          }
      }
      is WebSocketMmEvent.Data.ChannelViewed -> {
        Maybe.fromCallable {
          val channelId = data.channel_id ?: throwExpectedNotNull("channel_viewed.data", "channel_id")
          ServerEvent.ChannelViewed(channelId)
        }
      }
    }
  }

}