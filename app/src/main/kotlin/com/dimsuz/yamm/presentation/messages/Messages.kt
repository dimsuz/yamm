package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.presentation.baseui.HasLceState
import com.dimsuz.yamm.presentation.baseui.MviView

interface Messages {
  interface View : MviView<ViewState>

  data class ViewState(
    override val showProgressBar: Boolean,
    override val contentLoadingError: String?,
    val liveConnectionError: String?,
    val posts: List<Post>
  ) : HasLceState<String, ViewState> {

    override fun clearTransientState(): ViewState {
      return this.copy(showProgressBar = false, contentLoadingError = null, liveConnectionError = null)
    }
  }
}