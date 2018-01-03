package com.dimsuz.yamm.domain.models

sealed class ServerEvent {
  object Unknown: ServerEvent()
  object UserTyping : ServerEvent()

  data class Posted(
    val channelId: String,
    val teamId: String,
    val channelName: String,
    val postId: String) : ServerEvent()

  data class ChannelViewed(
    val channelId: String) : ServerEvent()
}
