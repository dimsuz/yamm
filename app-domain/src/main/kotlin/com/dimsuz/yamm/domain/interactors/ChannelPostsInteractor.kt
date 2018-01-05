package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.domain.models.Post
import io.reactivex.Observable

interface ChannelPostsInteractor {
  fun stateEvents(): Observable<ChannelPostEvent>
  fun setChannel(channelId: String)
  fun addPost(message: String)
  fun loadAnotherPage()
  fun setForegroundStateChanges(changes: Observable<Boolean>)
  fun resetForegroundStateChanges()
  fun channelPosts(): Observable<List<Post>>
}

sealed class ChannelPostEvent {
  class Loading : ChannelPostEvent()
  data class LoadFailed(val error: Throwable) : ChannelPostEvent()
  class Idle : ChannelPostEvent()
  data class LiveConnectionFailed(val error: Throwable) : ChannelPostEvent()
}

