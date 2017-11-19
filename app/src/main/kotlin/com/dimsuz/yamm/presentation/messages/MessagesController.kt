package com.dimsuz.yamm.presentation.messages

import android.support.v7.widget.Toolbar
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.MainActivity
import com.dimsuz.yamm.presentation.baseui.BaseController
import com.dimsuz.yamm.presentation.baseui.BindView
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance

class MessagesController : BaseController() {
  private val toolbar: Toolbar by BindView(R.id.toolbar)

  override fun initializeView(rootView: View) {
    appScope.instance<NavDrawerContextManager>().setContext(DrawerContextType.Messages)
  }

  override fun onAttach(view: View) {
    super.onAttach(view)
    // it is too early to setup nav drawer in initializeView - it may not be created by that time
    val navDrawerView = (activityUnsafe as MainActivity).navDrawerView
    navDrawerView.configureToolbar(toolbar)
  }

  override fun getViewLayout(): Int {
    return R.layout.messages
  }
}