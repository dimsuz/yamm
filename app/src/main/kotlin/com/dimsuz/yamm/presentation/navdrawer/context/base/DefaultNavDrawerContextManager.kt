package com.dimsuz.yamm.presentation.navdrawer.context.base

import com.dimsuz.yamm.core.util.checkMainThread
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class DefaultNavDrawerContextManager @Inject constructor(private val factory: NavDrawerContextFactory) : NavDrawerContextManager {
  private val currentContext = BehaviorSubject.create<NavDrawerContext>()

  override fun setContext(type: DrawerContextType) {
    checkMainThread()
    currentContext.onNext(createContext(type))
  }

  private fun createContext(type: DrawerContextType): NavDrawerContext {
    return factory.create(type)
  }

  override fun currentContext(): Observable<NavDrawerContext> {
    return currentContext
  }
}