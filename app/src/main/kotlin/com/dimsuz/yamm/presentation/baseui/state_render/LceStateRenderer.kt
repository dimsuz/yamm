package com.dimsuz.yamm.presentation.baseui.state_render

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dimsuz.yamm.presentation.baseui.HasLceState
import com.dimsuz.yamm.presentation.baseui.util.isVisible

open class LceStateRenderer<out E, in S : HasLceState<E, *>>(
  @IdRes
  private val contentViewId: Int,
  @IdRes
  private val progressBarViewId: Int,
  @LayoutRes
  private val errorLayoutResource: Int,
  private val errorDetailsRenderer: (View, E) -> Unit,
  private val additionalContentViewIds: IntArray? = null,
  /**
   * An id of the view into which error view should be inflated.
   * If 0 is passed it will default to the parent ViewGroup of
   * the view specified by contentViewId
   */
  @IdRes
  private val errorContainerViewId: Int = 0) : StateRenderer<S> {

  private var progressBar: View? = null
  private var contentView: ViewGroup? = null
  private var errorView: View? = null
  private var rootView: View? = null
  private var additionalContentViews: List<View>? = null

  override fun onViewCreated(view: View) {
    rootView = view
    progressBar = view.findViewById(progressBarViewId)
    contentView = view.findViewById(contentViewId)
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
    if (state.contentLoadingError != null) {
      errorDetailsRenderer.invoke(errorView!!, state.contentLoadingError!!)
    }
  }

  override fun onViewDestroyed() {
    rootView = null
    progressBar = null
    contentView = null
    errorView = null
    additionalContentViews = null
  }

  private fun ensureErrorViewCreated() {
    if (errorView != null) {
      return
    }
    val containerView = if (errorContainerViewId == 0) contentView!!.parent as ViewGroup else rootView!!.findViewById(errorContainerViewId)
    errorView = LayoutInflater.from(containerView.context).inflate(errorLayoutResource, containerView, false)
    containerView.addView(errorView)
  }

}