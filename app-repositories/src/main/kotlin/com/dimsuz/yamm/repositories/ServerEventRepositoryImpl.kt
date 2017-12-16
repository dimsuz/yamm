package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.models.WebSocketAuthJson
import com.dimsuz.yamm.data.sources.network.services.MattermostEventsApi
import com.dimsuz.yamm.data.sources.network.services.WebSocketEvent
import com.dimsuz.yamm.domain.models.ServerEvent
import com.dimsuz.yamm.domain.repositories.ServerEventRepository
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class ServerEventRepositoryImpl @Inject constructor(
  private val mattermostEventsApi: MattermostEventsApi,
  private val sessionManager: SessionManager) : ServerEventRepository {

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
            .map { it.toDomainModel() }
        } else {
          Completable
            .fromAction { mattermostEventsApi.closeConnection() }
            .andThen(Observable.never<ServerEvent>())
        }
      }
  }

}