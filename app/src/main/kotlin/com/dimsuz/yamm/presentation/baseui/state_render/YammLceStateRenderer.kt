package com.dimsuz.yamm.presentation.baseui.state_render

import android.support.annotation.IdRes
import android.view.View
import android.widget.TextView
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.baseui.HasLceState

class YammLceStateRenderer<in S : HasLceState<String, *>> (
  @IdRes contentViewId: Int = R.id.contentView,
  @IdRes progressBarViewId: Int = R.id.progressBar,
  additionalContentViewIds: IntArray? = null,
  @IdRes errorContainerViewId: Int = 0)

  : LceStateRenderer<String, S>(contentViewId, progressBarViewId, R.layout.screen_state_error,
  createErrorDetailsRenderer(), additionalContentViewIds, errorContainerViewId)

private fun createErrorDetailsRenderer(): (View, String) -> Unit {
  return { errorView, error ->
    val errorTitleView = errorView.findViewById<TextView>(R.id.errorText)
    errorTitleView.text = error
  }
}
