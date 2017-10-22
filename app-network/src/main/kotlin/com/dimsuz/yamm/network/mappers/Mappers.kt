package com.dimsuz.yamm.network.mappers

import com.dimsuz.yamm.domain.ServerConfig
import com.dimsuz.yamm.network.models.ServerConfigJson

internal fun ServerConfigJson.toDomainModel(): ServerConfig {
  return ServerConfig(
    isEmailSignInEnabled = this.EnableSignInWithEmail.toBoolean(),
    // this is weird, but SignUp means actually sign in
    // (I guess that's because its an SSO option)
    isGitlabSignInEnabled = this.EnableSignUpWithGitLab.toBoolean()
  )
}
