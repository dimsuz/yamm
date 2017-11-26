package com.dimsuz.yamm.presentation.messages

import android.support.v7.widget.Toolbar
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.MainActivity
import com.dimsuz.yamm.presentation.baseui.BindView
import com.dimsuz.yamm.presentation.baseui.ScopedMviController
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance
import toothpick.config.Module

class MessagesController : ScopedMviController<Messages.ViewState, Messages.View, MessagesPresenter>() {

  override fun createScopedConfig() = object : Config {
    override val viewLayoutResource = R.layout.messages
    override val screenModule = object : Module() {
      init {
        bind(MessagesPresenter::class.java).to(MessagesPresenter::class.java)
      }
    }
  }

  private val toolbar: Toolbar by BindView(R.id.toolbar)

  override fun createPresenter(): MessagesPresenter {
    return screenScope.instance()
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
