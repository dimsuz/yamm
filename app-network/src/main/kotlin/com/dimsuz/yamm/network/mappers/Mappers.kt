package com.dimsuz.yamm.network.mappers

import com.dimsuz.yamm.network.models.ServerConfigJson

data class ServerConfig(
  val isEmailSignInEnabled: Boolean,
  val isGitlabSignInEnabled: Boolean
)

internal fun ServerConfigJson.toDomainModel(): ServerConfig {
  return ServerConfig(
    isEmailSignInEnabled = this.EnableSignInWithEmail,
    // this is weird, but SignUp means actually sign in
    // (I guess that's because its an SSO option)
    isGitlabSignInEnabled = this.EnableSignUpWithGitLab
  )
}
