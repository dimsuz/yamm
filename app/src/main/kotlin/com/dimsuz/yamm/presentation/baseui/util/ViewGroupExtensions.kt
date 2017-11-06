package com.dimsuz.yamm.presentation.baseui.util

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.children(): List<View> {
  return (0 until childCount).map(this::getChildAt)
}

inline fun ViewGroup.forEach(action: (View) -> Unit) {
  for (i in (0 until childCount)) {
    action(getChildAt(i))
  }
}

inline fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
  for (i in (0 until childCount)) {
    action(i, getChildAt(i))
  }
}

/**
 * Sets visibility of children matching a [predicate] either to View.VISIBLE or to View.GONE based on [isVisible] value
 */
inline fun ViewGroup.setVisible(isVisible: Boolean, predicate: (View) -> Boolean) {
  @Suppress("LoopToCallChain")
  for (i in (0 until childCount)) {
    val view = getChildAt(i)
    if (predicate(view)) {
      view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
  }
}

/**
 * Sets visibility of children matching  a [predicate] to [visibility]
 */
inline fun ViewGroup.setVisibility(visibility: Int, predicate: (View) -> Boolean) {
  @Suppress("LoopToCallChain")
  for (i in (0 until childCount)) {
    val view = getChildAt(i)
    if (predicate(view)) {
      view.visibility = visibility
    }
  }
}

/**
 * Sets visibility of children matching [predicate] to [visibilityMatching] and others to [visibilityNotMatching]
 */
inline fun ViewGroup.switchVisibility(predicate: (View) -> Boolean, visibilityMatching: Int = View.VISIBLE, visibilityNotMatching: Int = View.GONE) {
  @Suppress("LoopToCallChain")
  for (i in (0 until childCount)) {
    val view = getChildAt(i)
    if (predicate(view)) {
      view.visibility = visibilityMatching
    } else {
      view.visibility = visibilityNotMatching
    }
  }
}

/**
 * If this layout has less then [count] children, then additional views will be inflated
 * and added to the layout. If [count] is sufficient, nothing will be done
 */
fun ViewGroup.ensureChildren(count: Int, @LayoutRes layoutResource: Int, visibilityForExtraChildren: Int = View.GONE) {
  require(count >= 0) { "count must be a positive integer, got $count"}
  val neededCount = count - childCount
  if (neededCount > 0) {
    val inflater = LayoutInflater.from(context)
    for (i in (0 until neededCount)) {
      inflater.inflate(layoutResource, this, true)
    }
  } else {
    for (i in (count until childCount)) {
      getChildAt(i).visibility = visibilityForExtraChildren
    }
  }
}
