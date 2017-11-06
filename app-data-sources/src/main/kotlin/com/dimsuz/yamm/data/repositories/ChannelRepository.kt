package com.dimsuz.yamm.data.repositories

import com.dimsuz.yamm.data.sources.network.mappers.toDomainModel
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Channel
import io.reactivex.Observable
import javax.inject.Inject

class ChannelRepository @Inject internal constructor(private val serviceApi: MattermostAuthorizedApi) {

  fun userChannels(userId: String, teamId: String): Observable<List<Channel>> {
    return serviceApi.getUserChannels(userId, teamId)
      .map { channels -> channels.map { it.toDomainModel() } }
      .toObservable()
  }

}