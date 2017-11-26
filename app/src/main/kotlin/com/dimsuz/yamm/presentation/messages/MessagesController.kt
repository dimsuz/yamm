package com.dimsuz.yamm.presentation.messages

import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.MainActivity
import com.dimsuz.yamm.presentation.baseui.BaseMviController
import com.dimsuz.yamm.presentation.baseui.BindView
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

class MessagesController : BaseMviController<Messages.ViewState, Messages.View, MessagesPresenter>() {
  override val config: Config
    get() = object : Config {
      override val viewLayoutResource = R.layout.messages
    }

  private lateinit var screenScope: Scope

  private val toolbar: Toolbar by BindView(R.id.toolbar)

  override fun onContextAvailable(context: Context) {
    super.onContextAvailable(context)
    screenScope = Toothpick.openScopes(appScope.name, this)
    screenScope.installModules(object : Module() {
      init {
        bind(MessagesPresenter::class.java).to(MessagesPresenter::class.java)
      }
    })
  }

  override fun createPresenter(): MessagesPresenter {
    return screenScope.instance()
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(screenScope.name)
  }

  override fun initializeView(rootView: View) {
    appScope.instance<NavDrawerContextManager>().setContext(DrawerContextType.Messages)
  }

  override fun onAttach(view: View) {
    super.onAttach(view)
    // it is too early to setup nav drawer in initializeView - it may not be created by that time
    val navDrawerView = (activityUnsafe as MainActivity).navDrawerView
    navDrawerView.configureToolbar(toolbar)
  }

  override fun renderViewState(viewState: Messages.ViewState) {
  }

}
