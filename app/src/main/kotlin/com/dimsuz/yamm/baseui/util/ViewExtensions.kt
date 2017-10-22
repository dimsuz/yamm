package com.dimsuz.yamm.baseui.util

import android.view.View

var View.isVisible: Boolean
  get() = visibility == View.VISIBLE
  set(value) {
    visibility = if (value) View.VISIBLE else View.GONE
  }