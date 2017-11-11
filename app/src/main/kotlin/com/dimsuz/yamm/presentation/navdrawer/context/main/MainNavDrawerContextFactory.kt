package com.dimsuz.yamm.presentation.navdrawer.context.main

import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerItem
import io.reactivex.Observable
import javax.inject.Inject

class MainNavDrawerContextFactory @Inject constructor(): NavDrawerContextFactory {
  override fun create(type: DrawerContextType): NavDrawerContext {
    return when (type) {
      DrawerContextType.Channels -> createChannelsContext()
      DrawerContextType.Teams -> TODO("not implemented")
    }
  }

  private fun createChannelsContext(): NavDrawerContext {
    return NavDrawerContext(
      DrawerContextType.Channels,
      Observable.just(
        listOf(
          NavDrawerItem(1, "hello"),
          NavDrawerItem(2, "hello1"),
          NavDrawerItem(3, "hello2"),
          NavDrawerItem(4, "hello3"),
          NavDrawerItem(5, "hello4")
        )
      )
    )
  }
}