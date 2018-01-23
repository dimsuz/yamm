package com.dimsuz.markdown

import android.text.SpannableString
import com.dimsuz.markdown.renderer.SpannableContentRenderer
import org.commonmark.parser.Parser

class YammMarkdownRenderer {
  fun render(text: String): SpannableString {
    val parser = Parser.builder().build()
    val document = parser.parse(text)
    return SpannableContentRenderer().renderToSpannable(document)
  }
}