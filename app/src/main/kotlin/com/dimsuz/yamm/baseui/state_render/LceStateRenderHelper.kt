package com.dimsuz.yamm.baseui.state_render

import android.view.View
import android.view.ViewGroup
import com.dimsuz.yamm.baseui.HasLceState

class LceStateRenderHelper<in S : HasLceState<*, *>>(
  private val contentViewId: Int,
  private val progressBarViewId: Int,
  private val additionalContentViewIds: IntArray?) : StateRenderer<S> {

  private var progressBar: View? = null
  private var contentView: ViewGroup? = null
  private var additionalContentViews: List<View>? = null

  override fun onViewCreated(view: View) {
    progressBar = view.findViewById(progressBarViewId)
    contentView = view.findViewById(contentViewId)
    additionalContentViews = additionalContentViewIds?.map { view.findViewById<View>(it) }
  }

  override fun render(state: S, previousState: S?) {
    if (previousState != null && previousState.showProgressBar == state.showProgressBar
      && previousState.contentLoadingError == state.contentLoadingError) {
      return
    }
    // TODO
  }

  override fun onViewDestroyed() {
    progressBar = null
    contentView = null
    additionalContentViews = null
  }
}