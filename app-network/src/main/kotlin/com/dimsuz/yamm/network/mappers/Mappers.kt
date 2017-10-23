package com.dimsuz.yamm.network.mappers

import com.dimsuz.yamm.domain.errors.ModelMapException
import com.dimsuz.yamm.domain.models.ServerConfig
import com.dimsuz.yamm.domain.models.Team
import com.dimsuz.yamm.network.models.ServerConfigJson
import com.dimsuz.yamm.network.models.TeamJson

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
