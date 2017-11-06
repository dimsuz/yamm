package com.dimsuz.yamm.presentation.navdrawer.context.base

import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import io.reactivex.Observable

interface NavDrawerContextManager {
  fun setContext(type: DrawerContextType)
  fun currentContext(): Observable<NavDrawerContext>
}