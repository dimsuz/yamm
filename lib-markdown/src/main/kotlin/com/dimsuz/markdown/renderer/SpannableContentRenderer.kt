package com.dimsuz.markdown.renderer

import android.text.SpannableString
import android.text.SpannableStringBuilder
import org.commonmark.node.Node
import org.commonmark.renderer.Renderer
import java.lang.Appendable

internal class SpannableContentRenderer : Renderer {

  fun renderToSpannable(node: Node): SpannableString {
    val sb = SpannableStringBuilder()
    render(node, sb)
    return SpannableString(sb)
  }

  override fun render(node: Node, output: Appendable) {
    if (output !is SpannableStringBuilder) {
      throw RuntimeException("only SpannableStringBuilder is accepted as output")
    }
    val buildVisitor = SpannableBuildVisitor(SpannableWriter(output))
    node.accept(LoggingVisitor())
    node.accept(buildVisitor)
  }

  override fun render(node: Node): String {
    // TODO warn that this overload won't do what's desired? (will strip spans)
    return renderToSpannable(node).toString()
  }
}