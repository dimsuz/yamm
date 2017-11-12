package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.db.persistence.ChannelPersistence
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.repositories.mappers.toDatabaseModel
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class ChannelRepositoryImpl @Inject internal constructor(
  private val serviceApi: MattermostAuthorizedApi,
  private val channelPersistence: ChannelPersistence) : ChannelRepository {

  override fun refreshUserChannels(userId: String, teamId: String): Completable {
    return serviceApi.getUserChannels(userId, teamId)
      .flatMapCompletable { channels ->
        Completable.fromAction {
          val dbChannels = channels.map { it.toDatabaseModel(userId, teamId) }
          channelPersistence.replaceUserChannels(dbChannels)
        }
      }
  }

  @Suppress("ReplaceGetOrSet")
  override fun userChannelsLive(userId: String, teamId: String): Observable<List<Channel>> {
    return channelPersistence.getUserChannelsLive(userId, teamId)
      .map { dbChannels -> dbChannels.map { it.toDomainModel() } }
  }
}

