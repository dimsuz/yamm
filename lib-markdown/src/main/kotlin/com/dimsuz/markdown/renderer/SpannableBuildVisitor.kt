package com.dimsuz.markdown.renderer

import org.commonmark.node.AbstractVisitor
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
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak

class SpannableBuildVisitor(private val writer: SpannableWriter) : AbstractVisitor() {

  override fun visit(blockQuote: BlockQuote) {
    super.visit(blockQuote)
  }

  override fun visit(bulletList: BulletList) {
    super.visit(bulletList)
  }

  override fun visit(code: Code) {
    super.visit(code)
  }

  override fun visit(document: Document) {
    super.visit(document)
  }

  override fun visit(emphasis: Emphasis) {
    super.visit(emphasis)
  }

  override fun visit(fencedCodeBlock: FencedCodeBlock) {
    super.visit(fencedCodeBlock)
  }

  override fun visit(hardLineBreak: HardLineBreak) {
    super.visit(hardLineBreak)
  }

  override fun visit(heading: Heading) {
    super.visit(heading)
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
    super.visit(listItem)
  }

  override fun visit(orderedList: OrderedList) {
    super.visit(orderedList)
  }

  override fun visit(paragraph: Paragraph) {
    super.visit(paragraph)
  }

  override fun visit(softLineBreak: SoftLineBreak) {
    super.visit(softLineBreak)
  }

  override fun visit(strongEmphasis: StrongEmphasis) {
    super.visit(strongEmphasis)
  }

  override fun visit(text: Text) {
    super.visit(text)
  }

  override fun visit(customBlock: CustomBlock) {
    super.visit(customBlock)
  }

  override fun visit(customNode: CustomNode) {
    super.visit(customNode)
  }
}