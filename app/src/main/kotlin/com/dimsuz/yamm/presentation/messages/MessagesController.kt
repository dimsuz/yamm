package com.dimsuz.yamm.presentation.messages

import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.baseui.BaseController
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance

class MessagesController : BaseController() {
  override fun initializeView(rootView: View) {
    appScope.instance<NavDrawerContextManager>().setContext(DrawerContextType.Messages)
  }

  override fun getViewLayout(): Int {
    return R.layout.messages
  }
}