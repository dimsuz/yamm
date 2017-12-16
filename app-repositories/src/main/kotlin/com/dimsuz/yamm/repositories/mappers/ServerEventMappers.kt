package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.network.services.WebSocketEvent
import com.dimsuz.yamm.domain.models.ServerEvent

fun WebSocketEvent.toDomainModel(): ServerEvent {
  return when (this) {
    is WebSocketEvent.TextMessage -> {
      ServerEvent.Posted // TODO use actual event
    }
    else -> throw RuntimeException("unsupported web socket event: $this")
  }
}