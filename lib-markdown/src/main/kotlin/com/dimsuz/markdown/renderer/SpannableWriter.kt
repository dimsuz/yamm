package com.dimsuz.markdown.renderer

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.BulletSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import java.util.*

private const val DEFAULT_TEXT_SIZE_SP = 14

internal class SpannableWriter(private val builder: SpannableStringBuilder) {
  private val textSizeStack = ArrayDeque<Int>().apply { push(DEFAULT_TEXT_SIZE_SP) }
  private val emphasisStack = ArrayDeque<TextEmphasis>()
  private var bulletItemStartIndex = Integer.MIN_VALUE
  private var orderedItemStartIndex = Integer.MIN_VALUE

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

  fun startBulletList() {
    bulletItemStartIndex = -1
  }

  fun endBulletList() {
    bulletItemStartIndex = Integer.MIN_VALUE
  }

  fun startOrderedList() {
    orderedItemStartIndex = -1
  }

  fun endOrderedList() {
    orderedItemStartIndex = Integer.MIN_VALUE
  }

  fun startListItem() {
    if (bulletItemStartIndex != Integer.MIN_VALUE) {
      bulletItemStartIndex = builder.length
    } else if (orderedItemStartIndex != Integer.MIN_VALUE) {
      orderedItemStartIndex = builder.length
    }
  }

  fun endListItem() {
    if (bulletItemStartIndex != Integer.MIN_VALUE) {
      check(bulletItemStartIndex != -1) { "expected a valid start index for bullet list item" }
      applyBulletListItemSpan(bulletItemStartIndex, builder.length)
    } else if (orderedItemStartIndex != Integer.MIN_VALUE) {
      check(orderedItemStartIndex != -1) { "expected a valid start index for ordered list item" }
      applyBulletListItemSpan(orderedItemStartIndex, builder.length)
    }
  }

  fun text(text: String) {
    builder.append(text)
    applyTextSizeSpan(text)
    applyEmphasisSpan(text)
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

  private fun applyEmphasisSpan(text: String) {
    val emphasis = emphasisStack.peek() ?: return
    val span: Any = when (emphasis) {
      TextEmphasis.Italic -> {
        StyleSpan(Typeface.ITALIC)
      }
      TextEmphasis.Strong -> {
        StyleSpan(Typeface.BOLD)
      }
      TextEmphasis.StrikeThrough -> {
        StrikethroughSpan()
      }
      TextEmphasis.Underline -> {
        UnderlineSpan()
      }
    }
    builder.setSpan(
      span,
      builder.length - text.length,
      builder.length,
      Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
  }

  private fun applyBulletListItemSpan(startIndex: Int, endIndex: Int) {
    // list item contents can be a paragraph and adding a paragraph usually adds new line (if it's not first
    // in sequence). But list item span must start at an actual non-ln char, find it.
    // (take care not to overflow during search in case something goes wrong)
    var nonLnStartIndex = startIndex
    while (builder[nonLnStartIndex] == '\n' && nonLnStartIndex != builder.length) nonLnStartIndex++
    if (nonLnStartIndex < endIndex) {
      builder.setSpan(BulletSpan(16), nonLnStartIndex, endIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  private fun applyOrderedListItemSpan(startIndex: Int, endIndex: Int) {
  }

  fun line() {
    builder.appendln()
  }

}
