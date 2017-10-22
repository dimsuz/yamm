package com.dimsuz.yamm.login.method_select

import com.dimsuz.yamm.baseui.HasLceState
import com.dimsuz.yamm.baseui.MviView

interface LoginMethodSelect {
  interface View : MviView<ViewState>

  data class ViewState(
    override val showProgressBar: Boolean,
    override val contentLoadingError: String?,
    val showGitlabSignIn: Boolean,
    val showEmailSignIn: Boolean
  ) : HasLceState<String, ViewState> {
    override fun clearTransientState() = copy(showProgressBar = false, contentLoadingError = null)
  }
}
