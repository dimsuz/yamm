package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Channel
import io.reactivex.Observable
import io.reactivex.Single

interface ChannelRepository {
  fun userChannels(userId: String, teamId: String): Single<List<Channel>>
  fun userChannelsLive(userId: String, teamId: String): Observable<List<Channel>>
}