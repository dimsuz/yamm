package com.dimsuz.markdown.renderer

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Block
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.CustomBlock
import org.commonmark.node.CustomNode
import org.commonmark.node.Document
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak

internal class SpannableBuildVisitor(private val writer: SpannableWriter) : AbstractVisitor() {

  override fun visit(blockQuote: BlockQuote) {
    super.visit(blockQuote)
  }

  override fun visit(bulletList: BulletList) {
    writer.startBulletList()
    visitChildren(bulletList)
    writer.endBulletList()
  }

  override fun visit(code: Code) {
    writer.inlineText(code.literal)
  }

  override fun visit(document: Document) {
    writer.start()
    visitChildren(document)
    writer.end()
  }

  override fun visit(emphasis: Emphasis) {
    writer.pushEmphasis(TextEmphasis.Italic)
    visitChildren(emphasis)
    writer.popEmphasis()
  }

  override fun visit(fencedCodeBlock: FencedCodeBlock) {
    super.visit(fencedCodeBlock)
  }

  override fun visit(hardLineBreak: HardLineBreak) {
    super.visit(hardLineBreak)
  }

  override fun visit(heading: Heading) {
    writer.pushTextSize(getHeadingTextSize(heading.level))
    visitChildren(heading)
    writer.popTextSize()
  }

  override fun visit(thematicBreak: ThematicBreak) {
    super.visit(thematicBreak)
  }

  override fun visit(htmlInline: HtmlInline) {
    super.visit(htmlInline)
  }

  override fun visit(htmlBlock: HtmlBlock) {
    super.visit(htmlBlock)
  }

  override fun visit(image: Image) {
    super.visit(image)
  }

  override fun visit(indentedCodeBlock: IndentedCodeBlock) {
    super.visit(indentedCodeBlock)
  }

  override fun visit(link: Link) {
    super.visit(link)
  }

  override fun visit(listItem: ListItem) {
    writer.startListItem()
    visitChildren(listItem)
    writer.endListItem()
  }

  override fun visit(orderedList: OrderedList) {
    super.visit(orderedList)
  }

  override fun visit(paragraph: Paragraph) {
    visitChildren(paragraph)
  }

  override fun visit(softLineBreak: SoftLineBreak) {
    super.visit(softLineBreak)
  }

  override fun visit(strongEmphasis: StrongEmphasis) {
    writer.pushEmphasis(TextEmphasis.Strong)
    visitChildren(strongEmphasis)
    writer.popEmphasis()
  }

  override fun visit(text: Text) {
    writer.text(text.literal)
  }

  override fun visit(customBlock: CustomBlock) {
    super.visit(customBlock)
  }

  override fun visit(customNode: CustomNode) {
    super.visit(customNode)
  }

  override fun visitChildren(node: Node) {
    if (node is Block && node !is Document) {
      if (!node.isFirstChild()) {
        writer.line()
        writer.line()
      }
    }
    super.visitChildren(node)
  }
}

private fun getHeadingTextSize(level: Int): Int {
  return when (level) {
    1 -> 56
    2 -> 45
    3 -> 34
    4 -> 24
    5 -> 20
    6 -> 16
    else -> 16
  }
}

private fun Node.isFirstChild(): Boolean {
  return this.parent.firstChild == this
}
