package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class ChannelRepositoryImpl @Inject internal constructor(private val serviceApi: MattermostAuthorizedApi) : ChannelRepository {

  override fun userChannels(userId: String, teamId: String): Single<List<Channel>> {
    return serviceApi.getUserChannels(userId, teamId)
      .map { channels -> channels.map { it.toDomainModel() } }
      .toObservable()
  }

}

