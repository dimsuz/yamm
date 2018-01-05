package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.dimsuz.yamm.data.sources.db.models.ChannelResolvedDbModel
import io.reactivex.Observable

interface ChannelPersistence {
  fun replaceUserChannels(channels: List<ChannelDbModel>)
  fun getUserChannels(userId: String, teamId: String): List<ChannelDbModel>
  fun getUserChannelsLive(userId: String, teamId: String): Observable<List<ChannelResolvedDbModel>>
  fun getChannelById(channelId: String): ChannelResolvedDbModel?
  fun getChannelByName(name: String, teamId: String): String?
  fun getUserChannelIds(userId: String, teamId: String): List<String>
}