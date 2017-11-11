package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

internal class ChannelRepositoryImpl @Inject internal constructor(private val serviceApi: MattermostAuthorizedApi) : ChannelRepository {
  // a cache userId -> teamId -> [ channels ]
  private val dataCache = BehaviorSubject.create<Map<String, Map<String, List<Channel>>>>().toSerialized()

  override fun userChannels(userId: String, teamId: String): Single<List<Channel>> {
    return serviceApi.getUserChannels(userId, teamId)
      .map { channels -> channels.map { it.toDomainModel() } }
      .doOnSuccess { channels -> updateChannelsInCache(userId, teamId, channels) }
  }

  @Suppress("ReplaceGetOrSet")
  override fun userChannelsLive(userId: String, teamId: String): Observable<List<Channel>> {
    return dataCache.map { it.get(userId)?.get(teamId) ?: emptyList() }
  }

  private fun updateChannelsInCache(userId: String, teamId: String, channels: List<Channel>) {
    val previousValue = dataCache.firstElement().defaultIfEmpty(emptyMap()).blockingGet()
    val updatedCache = previousValue.toMutableMap()
    val teamChannels = updatedCache[userId].orEmpty().toMutableMap()
    teamChannels[teamId] = channels
    updatedCache[userId] = teamChannels
    dataCache.onNext(updatedCache)
  }
}

