package com.dimsuz.yamm.login.method_select

import com.dimsuz.yamm.baseui.MviView

interface LoginMethodSelect {
  interface View : MviView<ViewState>

  data class ViewState(
    val showProgressBar: Boolean,
    val loadingError: String?,
    val showGitlabSignIn: Boolean,
    val showEmailSignIn: Boolean
  ) {
    fun clearTransientState() = copy(showProgressBar = false, loadingError = null)
  }
}
