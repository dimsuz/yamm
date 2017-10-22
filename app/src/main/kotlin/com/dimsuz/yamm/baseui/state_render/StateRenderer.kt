package com.dimsuz.yamm.baseui.state_render

import android.view.View

interface StateRenderer<in S> {
  fun onViewCreated(view: View)
  fun render(state: S, previousState: S?)
  fun onViewDestroyed()
}