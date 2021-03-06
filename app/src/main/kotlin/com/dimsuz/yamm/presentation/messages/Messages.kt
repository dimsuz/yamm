package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.presentation.baseui.HasLceState
import com.dimsuz.yamm.presentation.baseui.MviView
import io.reactivex.Observable

internal interface Messages {
  interface View : MviView<ViewState> {
    fun postInputTextChangedIntent(): Observable<String>
    fun sendPostIntent(): Observable<String>
  }

  data class ViewState(
    override val showProgressBar: Boolean,
    override val contentLoadingError: String?,
    val liveConnectionError: String?,
    val posts: List<Post>,
    val postDraft: String?,
    val sendButtonVisible: Boolean,
    val attachButtonVisible: Boolean
  ) : HasLceState<String, ViewState> {

    override fun clearTransientState(): ViewState {
      return this.copy(showProgressBar = false, contentLoadingError = null, liveConnectionError = null)
    }
  }
}