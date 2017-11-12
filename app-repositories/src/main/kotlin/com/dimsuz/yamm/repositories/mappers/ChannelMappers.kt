package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.dimsuz.yamm.data.sources.network.models.ChannelJson
import com.dimsuz.yamm.domain.errors.ModelMapException
import com.dimsuz.yamm.domain.models.Channel

internal fun ChannelJson.toDatabaseModel(userId: String, teamId: String): ChannelDbModel {
  return ChannelDbModel(
    id = this.id ?: throw ModelMapException("channel without id: $this"),
    userId = userId,
    teamId = teamId,
    type = this.type,
    displayName = this.display_name,
    name = this.name,
    header = this.header,
    purpose = this.purpose,
    teamMateId = extractTeamMateUserId(this.type, this.name, userId)
  )
}

internal fun ChannelDbModel.toDomainModel(): Channel {
  return Channel(
    id = this.id,
    type = this.type.toChannelType(),
    name = this.name,
    displayName = this.displayName,
    header = this.header,
    purpose = this.purpose
  )
}


private fun extractTeamMateUserId(type: String, name: String?, userId: String): String? {
  if (type.toChannelType() != Channel.Type.Direct || name == null || name.isBlank()) return null
  // for direct channels "name" contains ids of two participating users.
  // order can be different depending on who wrote to whom.
  // one of the users is always a currently authorized user.
  val (from, to) = name.split("__")
  return when (userId) {
    from -> to
    to -> from
    else -> null
  }
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
