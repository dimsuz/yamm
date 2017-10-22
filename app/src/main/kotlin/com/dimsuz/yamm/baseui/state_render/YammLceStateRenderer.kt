package com.dimsuz.yamm.baseui.state_render

import android.support.annotation.IdRes
import android.view.View
import android.widget.TextView
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.HasLceState

class YammLceStateRenderer<in S : HasLceState<String, *>> (
  @IdRes contentViewId: Int = R.id.content_view,
  @IdRes progressBarViewId: Int = R.id.progress_bar,
  additionalContentViewIds: IntArray? = null,
  @IdRes errorContainerViewId: Int = 0)

  : LceStateRenderer<String, S>(contentViewId, progressBarViewId, R.layout.screen_state_error,
  createErrorDetailsRenderer(), additionalContentViewIds, errorContainerViewId)

private fun createErrorDetailsRenderer(): (View, String) -> Unit {
  return { errorView, error ->
    val errorTitleView = errorView.findViewById<TextView>(R.id.error_text)
    errorTitleView.text = error
  }
}
