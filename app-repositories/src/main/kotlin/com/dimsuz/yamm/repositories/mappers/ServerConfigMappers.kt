package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.network.models.ServerConfigJson
import com.dimsuz.yamm.domain.models.ServerConfig

internal fun ServerConfigJson.toDomainModel(): ServerConfig {
  return ServerConfig(
    isEmailSignInEnabled = this.EnableSignInWithEmail.toBoolean(),
    // this is weird, but SignUp means actually sign in
    // (I guess that's because its an SSO option)
    isGitlabSignInEnabled = this.EnableSignUpWithGitLab.toBoolean()
  )
}
