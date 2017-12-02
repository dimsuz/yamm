package com.dimsuz.yamm.presentation.navdrawer

import android.app.Activity
import android.support.v7.widget.Toolbar
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerItem
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

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

  private val itemClicks: PublishSubject<NavDrawerItem> = PublishSubject.create()

  private val presenter = createPresenter()

  init {
    presenter.attachView(this)
    navigationDrawer.setOnDrawerItemClickListener { _, _, drawerItem ->
      itemClicks.onNext(drawerItem.toNavDrawerItem())
      false
    }
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

  fun itemClicks(): Observable<NavDrawerItem> {
    return itemClicks
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
    .withTag(this)
}

private fun IDrawerItem<*, *>.toNavDrawerItem() = this.tag!! as NavDrawerItem
