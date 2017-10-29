package com.dimsuz.yamm.baseui.util

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.dimsuz.yamm.util.appScope

internal inline val Controller.resourcesUnsafe get() = this.resources!!
internal inline val Controller.activityUnsafe get() = this.activity!!
internal inline val Controller.appScope get() = activityUnsafe.appScope

private const val SLIDE_BOTTOM_PUSH_ANIM_DURATION = 300L
private const val SLIDE_BOTTOM_POP_ANIM_DURATION = 200L
private const val SLIDE_HORIZONTAL_PUSH_ANIM_DURATION = 300L
private const val SLIDE_HORIZONTAL_POP_ANIM_DURATION = 200L
private const val SLIDE_FADE_PUSH_ANIM_DURATION = 300L
private const val SLIDE_FADE_POP_ANIM_DURATION = 200L

fun Controller.obtainVerticalTransaction(): RouterTransaction {
  return RouterTransaction
    .with(this)
    .pushChangeHandler(VerticalChangeHandler(SLIDE_BOTTOM_PUSH_ANIM_DURATION))
    .popChangeHandler(VerticalChangeHandler(SLIDE_BOTTOM_POP_ANIM_DURATION))
}

fun Controller.obtainHorizontalTransaction(): RouterTransaction {
  return RouterTransaction
    .with(this)
    .pushChangeHandler(HorizontalChangeHandler(SLIDE_HORIZONTAL_PUSH_ANIM_DURATION))
    .popChangeHandler(HorizontalChangeHandler(SLIDE_HORIZONTAL_POP_ANIM_DURATION))
}

fun Controller.obtainFadeTransaction(): RouterTransaction {
  return RouterTransaction
    .with(this)
    .pushChangeHandler(FadeChangeHandler(SLIDE_FADE_PUSH_ANIM_DURATION))
    .popChangeHandler(FadeChangeHandler(SLIDE_FADE_POP_ANIM_DURATION))
}

fun Router.pushControllerHorizontal(controller: Controller) {
  return pushController(controller.obtainHorizontalTransaction())
}

fun Router.pushControllerVertical(controller: Controller) {
  return pushController(controller.obtainVerticalTransaction())
}

fun Router.pushControllerFade(controller: Controller) {
  return pushController(controller.obtainFadeTransaction())
}
