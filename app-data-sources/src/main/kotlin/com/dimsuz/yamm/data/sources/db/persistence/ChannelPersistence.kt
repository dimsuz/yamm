package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import io.reactivex.Observable

interface ChannelPersistence {
  fun replaceUserChannels(channels: List<ChannelDbModel>)
  fun getUserChannels(userId: String, teamId: String): List<ChannelDbModel>
  fun getUserChannelsLive(userId: String, teamId: String): Observable<List<ChannelDbModel>>
}