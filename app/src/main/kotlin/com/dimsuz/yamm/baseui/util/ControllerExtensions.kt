package com.dimsuz.yamm.baseui.util

import com.bluelinelabs.conductor.Controller

inline val Controller.resourcesUnsafe get() = this.resources!!

