package com.dimsuz.yamm.presentation.navdrawer

import android.app.Activity
import android.support.v7.widget.Toolbar
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerItem
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

class NavDrawerView private constructor(private val activity: Activity,
                                        private val contextManager: NavDrawerContextManager) {

  companion object {
    fun create(activity: Activity, contextManager: NavDrawerContextManager): NavDrawerView {
      return NavDrawerView(activity, contextManager)
    }
  }

  private var navigationDrawer: Drawer = DrawerBuilder()
    .withActivity(activity)
    .withActionBarDrawerToggle(true)
    .build()

  private val presenter = createPresenter()

  init {
    presenter.attachView(this)
  }

  private fun createPresenter(): NavDrawerPresenter {
    return NavDrawerPresenter(contextManager)
  }

  fun configureToolbar(toolbar: Toolbar) {
    navigationDrawer.setToolbar(activity, toolbar)
  }

  fun cleanup() {
    presenter.detachView()
  }

  fun render(items: List<NavDrawerItem>) {
    navigationDrawer.removeAllItems()
    navigationDrawer.addItems(*items.map({ it.toIDrawerItem() }).toTypedArray())
  }
}

private fun NavDrawerItem.toIDrawerItem(): IDrawerItem<*, *> {
  return PrimaryDrawerItem()
    .withIdentifier(this.id)
    .withName(this.title)
}

