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
import timber.log.Timber

class LoggingVisitor : AbstractVisitor() {

  override fun visit(blockQuote: BlockQuote) {
    Timber.d("visiting $blockQuote")
    super.visit(blockQuote)
  }

  override fun visit(bulletList: BulletList) {
    Timber.d("visiting $bulletList")
    super.visit(bulletList)
  }

  override fun visit(code: Code) {
    Timber.d("visiting $code")
    super.visit(code)
  }

  override fun visit(document: Document) {
    Timber.d("visiting $document")
    super.visit(document)
  }

  override fun visit(emphasis: Emphasis) {
    Timber.d("visiting $emphasis")
    super.visit(emphasis)
  }

  override fun visit(fencedCodeBlock: FencedCodeBlock) {
    Timber.d("visiting $fencedCodeBlock")
    super.visit(fencedCodeBlock)
  }

  override fun visit(hardLineBreak: HardLineBreak) {
    Timber.d("visiting $hardLineBreak")
    super.visit(hardLineBreak)
  }

  override fun visit(heading: Heading) {
    Timber.d("visiting $heading")
    super.visit(heading)
  }

  override fun visit(thematicBreak: ThematicBreak) {
    Timber.d("visiting $thematicBreak")
    super.visit(thematicBreak)
  }

  override fun visit(htmlInline: HtmlInline) {
    Timber.d("visiting $htmlInline")
    super.visit(htmlInline)
  }

  override fun visit(htmlBlock: HtmlBlock) {
    Timber.d("visiting $htmlBlock")
    super.visit(htmlBlock)
  }

  override fun visit(image: Image) {
    Timber.d("visiting $image")
    super.visit(image)
  }

  override fun visit(indentedCodeBlock: IndentedCodeBlock) {
    Timber.d("visiting $indentedCodeBlock")
    super.visit(indentedCodeBlock)
  }

  override fun visit(link: Link) {
    Timber.d("visiting $link")
    super.visit(link)
  }

  override fun visit(listItem: ListItem) {
    Timber.d("visiting $listItem")
    super.visit(listItem)
  }

  override fun visit(orderedList: OrderedList) {
    Timber.d("visiting $orderedList")
    super.visit(orderedList)
  }

  override fun visit(paragraph: Paragraph) {
    Timber.d("visiting $paragraph")
    super.visit(paragraph)
  }

  override fun visit(softLineBreak: SoftLineBreak) {
    Timber.d("visiting $softLineBreak")
    super.visit(softLineBreak)
  }

  override fun visit(strongEmphasis: StrongEmphasis) {
    Timber.d("visiting $strongEmphasis")
    super.visit(strongEmphasis)
  }

  override fun visit(text: Text) {
    Timber.d("visiting $text")
    super.visit(text)
  }

  override fun visit(customBlock: CustomBlock) {
    Timber.d("visiting $customBlock")
    super.visit(customBlock)
  }

  override fun visit(customNode: CustomNode) {
    Timber.d("visiting $customNode")
    super.visit(customNode)
  }
}