package com.dimsuz.yamm.data.sources.network.mappers

import com.dimsuz.yamm.data.sources.network.models.ServerConfigJson
import com.dimsuz.yamm.data.sources.network.models.TeamJson
import com.dimsuz.yamm.domain.errors.ModelMapException
import com.dimsuz.yamm.domain.models.ServerConfig
import com.dimsuz.yamm.domain.models.Team

internal fun ServerConfigJson.toDomainModel(): ServerConfig {
  return ServerConfig(
    isEmailSignInEnabled = this.EnableSignInWithEmail.toBoolean(),
    // this is weird, but SignUp means actually sign in
    // (I guess that's because its an SSO option)
    isGitlabSignInEnabled = this.EnableSignUpWithGitLab.toBoolean()
  )
}

internal fun TeamJson.toDomainModel(): Team {
  return Team(
    id = this.id ?: throw ModelMapException("team without id"),
    name = this.name ?: "unknown team",
    display_name = this.display_name ?: this.name ?: "unknown team",
    description = this.description.orEmpty()
  )
}
