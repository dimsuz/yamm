package com.dimsuz.yamm.baseui.state_render

import android.view.View
import android.widget.TextView
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.HasLceState

class YammLceStateRenderer<in S : HasLceState<String, *>> (
  contentViewId: Int = R.id.content_view,
  progressBarViewId: Int = R.id.progress_bar,
  additionalContentViewIds: IntArray? = null,
  errorContainerViewId: Int = 0)

  : LceStateRenderer<String, S>(contentViewId, progressBarViewId, R.layout.screen_state_error,
  createErrorDetailsRenderer(), additionalContentViewIds, errorContainerViewId)

private fun createErrorDetailsRenderer(): (View, String) -> Unit {
  return { errorView, error ->
    val errorTitleView = errorView.findViewById<TextView>(R.id.error_text)
    errorTitleView.text = error
  }
}
