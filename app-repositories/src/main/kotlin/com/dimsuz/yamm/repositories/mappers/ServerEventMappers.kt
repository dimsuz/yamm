package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.network.models.WebSocketMmEvent
import com.dimsuz.yamm.data.sources.network.services.WebSocketEvent
import com.dimsuz.yamm.domain.errors.throwExpectedNotNull
import com.dimsuz.yamm.domain.models.ServerEvent

fun WebSocketEvent.toDomainModel(): ServerEvent {
  return when (this) {
    is WebSocketEvent.TextMessage -> {
      buildServerEvent(this.event)
    }
    else -> throw RuntimeException("unsupported web socket event: $this")
  }
}

private fun buildServerEvent(event: WebSocketMmEvent?): ServerEvent {
  return if (event?.data != null) {
    createEventFromData(event.data!!) ?: ServerEvent.Unknown
  } else {
    // ??? there might be some events without data?...
    ServerEvent.Unknown
  }
}

fun createEventFromData(data: WebSocketMmEvent.Data): ServerEvent? {
  return when (data) {
    is WebSocketMmEvent.Data.Posted -> {
      TODO("figure out a way to save post along with the user and fetch it if absent")
//      val post = data.post?.toDomainModel() ?: throwExpectedNotNull("posted.data", "post")
//      ServerEvent.Posted(
//        channelId = data.post?.channel_id ?: throwExpectedNotNull("posted.data.post", "channel_id"),
//        channelName = data.channel_name ?: throwExpectedNotNull("posted.data", "channel_name"),
//        teamId = data.team_id ?: throwExpectedNotNull("posted.data", "team_id"),
//        post = post
//      )
    }
    is WebSocketMmEvent.Data.ChannelViewed -> {
      ServerEvent.ChannelViewed(
        channelId = data.channel_id ?: throwExpectedNotNull("channel_viewed.data", "channel_id")
      )
    }
  }
}
