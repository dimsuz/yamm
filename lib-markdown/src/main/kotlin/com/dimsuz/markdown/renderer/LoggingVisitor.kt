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
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import timber.log.Timber

internal class LoggingVisitor : AbstractVisitor() {

  override fun visit(blockQuote: BlockQuote) {
    log("$blockQuote")
    super.visit(blockQuote)
  }

  override fun visit(bulletList: BulletList) {
    log("$bulletList")
    super.visit(bulletList)
  }

  override fun visit(code: Code) {
    log("$code")
    super.visit(code)
  }

  override fun visit(document: Document) {
    log("$document")
    super.visit(document)
  }

  override fun visit(emphasis: Emphasis) {
    log("$emphasis")
    super.visit(emphasis)
  }

  override fun visit(fencedCodeBlock: FencedCodeBlock) {
    log("$fencedCodeBlock")
    super.visit(fencedCodeBlock)
  }

  override fun visit(hardLineBreak: HardLineBreak) {
    log("$hardLineBreak")
    super.visit(hardLineBreak)
  }

  override fun visit(heading: Heading) {
    log("$heading")
    super.visit(heading)
  }

  override fun visit(thematicBreak: ThematicBreak) {
    log("$thematicBreak")
    super.visit(thematicBreak)
  }

  override fun visit(htmlInline: HtmlInline) {
    log("$htmlInline")
    super.visit(htmlInline)
  }

  override fun visit(htmlBlock: HtmlBlock) {
    log("$htmlBlock")
    super.visit(htmlBlock)
  }

  override fun visit(image: Image) {
    log("$image")
    super.visit(image)
  }

  override fun visit(indentedCodeBlock: IndentedCodeBlock) {
    log("$indentedCodeBlock")
    super.visit(indentedCodeBlock)
  }

  override fun visit(link: Link) {
    log("$link")
    super.visit(link)
  }

  override fun visit(listItem: ListItem) {
    log("$listItem")
    super.visit(listItem)
  }

  override fun visit(orderedList: OrderedList) {
    log("$orderedList")
    super.visit(orderedList)
  }

  override fun visit(paragraph: Paragraph) {
    log("$paragraph")
    super.visit(paragraph)
  }

  override fun visit(softLineBreak: SoftLineBreak) {
    log("$softLineBreak")
    super.visit(softLineBreak)
  }

  override fun visit(strongEmphasis: StrongEmphasis) {
    log("$strongEmphasis")
    super.visit(strongEmphasis)
  }

  override fun visit(text: Text) {
    log("$text")
    super.visit(text)
  }

  override fun visit(customBlock: CustomBlock) {
    log("$customBlock")
    super.visit(customBlock)
  }

  override fun visit(customNode: CustomNode) {
    log("$customNode")
    super.visit(customNode)
  }

  private var indentLevel = 0

  override fun visitChildren(parent: Node?) {
    indentLevel++
    super.visitChildren(parent)
    indentLevel--
  }

  private fun log(message: String) {
    Timber.d(".".repeat(indentLevel*4) + message)
  }
}