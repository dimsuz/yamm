package com.dimsuz.yamm.presentation.navdrawer.models

import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import io.reactivex.Observable

data class NavDrawerContext(
  val type: DrawerContextType,
  val items: Observable<List<NavDrawerItem>>,
  val selectionObserver: (NavDrawerItem) -> Unit
)