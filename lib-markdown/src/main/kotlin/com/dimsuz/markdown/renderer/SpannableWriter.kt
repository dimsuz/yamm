package com.dimsuz.markdown.renderer

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import java.util.*

private const val DEFAULT_TEXT_SIZE_SP = 14

internal class SpannableWriter(private val builder: SpannableStringBuilder) {
  private val textSizeStack = ArrayDeque<Int>().apply { push(DEFAULT_TEXT_SIZE_SP) }
  private val emphasisStack = ArrayDeque<TextEmphasis>()

  fun start() {
  }

  fun end() {
  }

  fun pushTextSize(size: Int) {
    textSizeStack.push(size)
  }

  fun popTextSize() {
    if (textSizeStack.size > 1) {
      textSizeStack.pop()
    }
  }

  fun pushEmphasis(emphasis: TextEmphasis) {
    emphasisStack.push(emphasis)
  }

  fun popEmphasis() {
    emphasisStack.pollFirst()
  }

  fun text(text: String) {
    builder.append(text)
    applyTextSizeSpan(text)
  }

  private fun applyTextSizeSpan(text: String) {
    val textSize = textSizeStack.peek() ?: DEFAULT_TEXT_SIZE_SP
    if (textSize != DEFAULT_TEXT_SIZE_SP) {
      builder.setSpan(
        AbsoluteSizeSpan(textSize, true),
        builder.length - text.length,
        builder.length,
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  fun line() {
    builder.appendln()
  }

}
