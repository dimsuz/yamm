package com.dimsuz.yamm.presentation.navdrawer.context.base

import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext

interface NavDrawerContextFactory {
  fun create(type: DrawerContextType): NavDrawerContext
}