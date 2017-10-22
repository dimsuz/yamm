package com.dimsuz.yamm.baseui.state_render

import android.view.View
import android.view.ViewGroup
import com.dimsuz.yamm.baseui.HasLceState
import com.dimsuz.yamm.baseui.util.isVisible

class LceStateRenderer<in S : HasLceState<*, *>>(
  private val contentViewId: Int,
  private val progressBarViewId: Int,
  private val additionalContentViewIds: IntArray?) : StateRenderer<S> {

  private var progressBar: View? = null
  private var contentView: ViewGroup? = null
  private var errorView: View? = null
  private var additionalContentViews: List<View>? = null

  override fun onViewCreated(view: View) {
    progressBar = view.findViewById(progressBarViewId)
    contentView = view.findViewById(contentViewId)
    errorView = contentView
    additionalContentViews = additionalContentViewIds?.map { view.findViewById<View>(it) }
  }

  override fun render(state: S, previousState: S?) {
    if (previousState != null && previousState.showProgressBar == state.showProgressBar
      && previousState.contentLoadingError == state.contentLoadingError) {
      return
    }

    progressBar?.isVisible = state.contentLoadingError == null && state.showProgressBar
    val contentVisible = state.contentLoadingError == null && !state.showProgressBar
    contentView?.isVisible = contentVisible
    additionalContentViews?.map { it.isVisible = contentVisible }

    if (state.contentLoadingError != null) {
      ensureErrorViewCreated()
    }
    errorView?.isVisible = state.contentLoadingError != null
  }

  override fun onViewDestroyed() {
    progressBar = null
    contentView = null
    errorView = null
    additionalContentViews = null
  }

  private fun ensureErrorViewCreated() {
    // TODO
  }

}