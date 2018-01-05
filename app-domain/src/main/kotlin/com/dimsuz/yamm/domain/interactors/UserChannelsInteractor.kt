package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.domain.models.Channel
import io.reactivex.Observable

interface UserChannelsInteractor {
  fun setChannelId(channelId: String)
  fun userChannels(): Observable<List<Channel>>
  fun stateEvents(): Observable<UserChannelsEvent>
}

sealed class UserChannelsEvent {
  object     Loading : UserChannelsEvent()
  data class LoadFailed(val error: Throwable) : UserChannelsEvent()
  object     Idle : UserChannelsEvent()
}

