package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Channel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

interface ChannelRepository {
  fun getChannelIdByName(name: String, teamId: String): String?
  fun getChannelIds(userId: String, teamId: String): List<String>
  fun getChannelById(channelId: String): Maybe<Channel>

  fun refreshUserChannels(userId: String, teamId: String): Completable
  fun userChannelsLive(userId: String, teamId: String): Observable<List<Channel>>
}