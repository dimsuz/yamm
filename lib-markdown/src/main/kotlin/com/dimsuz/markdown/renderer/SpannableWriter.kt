package com.dimsuz.markdown.renderer

import android.text.SpannableStringBuilder
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
  }

  fun line() {
    builder.appendln()
  }

}
