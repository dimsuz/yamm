package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Channel
import io.reactivex.Completable
import io.reactivex.Observable

interface ChannelRepository {
  fun refreshUserChannels(userId: String, teamId: String): Completable
  fun userChannelsLive(userId: String, teamId: String): Observable<List<Channel>>
}