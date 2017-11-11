package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Channel
import io.reactivex.Observable

interface ChannelRepository {
  fun userChannels(userId: String, teamId: String): Observable<List<Channel>>
}