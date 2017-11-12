package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.network.models.TeamJson
import com.dimsuz.yamm.domain.errors.ModelMapException
import com.dimsuz.yamm.domain.models.Team

internal fun TeamJson.toDomainModel(): Team {
  return Team(
    id = this.id ?: throw ModelMapException("team without id: $this"),
    name = this.name ?: "unknown team",
    display_name = this.display_name ?: this.name ?: "unknown team",
    description = this.description.orEmpty()
  )
}

