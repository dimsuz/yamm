package com.dimsuz.yamm.presentation.navdrawer.context.main

import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import io.reactivex.Observable

class MainNonAuthorizedNavDrawerContextFactory : NavDrawerContextFactory {
  override fun create(type: DrawerContextType): NavDrawerContext {
    return NavDrawerContext(type, Observable.empty(), { })
  }
}