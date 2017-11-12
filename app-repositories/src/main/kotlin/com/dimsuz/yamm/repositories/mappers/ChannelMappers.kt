package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.network.models.ChannelJson
import com.dimsuz.yamm.domain.errors.ModelMapException
import com.dimsuz.yamm.domain.models.Channel

internal fun ChannelJson.toDomainModel(): Channel {
  return Channel(
    id = this.id ?: throw ModelMapException("channel without id: $this"),
    type = this.type.toChannelType(),
    name = this.name ?: "unknown channel",
    displayName = this.display_name ?: this.name ?: "unknown channel",
    header = this.header,
    purpose = this.purpose
  )
}

private fun String.toChannelType(): Channel.Type {
  return when (this) {
    "O" -> Channel.Type.Open
    "P" -> Channel.Type.Private
    "D" -> Channel.Type.Direct
    "G" -> Channel.Type.Group
    else -> Channel.Type.Unknown
  }
}
