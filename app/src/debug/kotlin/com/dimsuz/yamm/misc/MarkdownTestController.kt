package com.dimsuz.yamm.misc

import android.view.View
import com.dimsuz.markdown.YammMarkdownRenderer
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.baseui.BaseController
import kotlinx.android.synthetic.debug.markdown_test.*

class MarkdownTestController : BaseController() {
  private val renderer = YammMarkdownRenderer()

  override fun getViewLayout(): Int {
    return R.layout.markdown_test
  }

  override fun initializeView(rootView: View) {
    primaryButton.setOnClickListener { render(editText.text.toString()) }
    renderSampleButton.setOnClickListener { render(TEST_SIMPLE) }
  }

  private fun render(s: String) {
    markdownOutputView.text = renderer.render(s)
  }
}