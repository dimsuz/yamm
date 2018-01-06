package com.dimsuz.yamm.presentation.navdrawer.models

import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import io.reactivex.Observable

data class NavDrawerContext(
  val type: DrawerContextType,
  val items: Observable<ItemsState>,
  val selectionObserver: (NavDrawerItem) -> Unit
) {
  data class ItemsState(
    val items: List<NavDrawerItem>,
    val selectedItem: NavDrawerItem?
  )
}