package com.dimsuz.yamm.main

import com.bluelinelabs.conductor.Controller
import com.dimsuz.yamm.baseui.BaseControllerActivity
import com.dimsuz.yamm.data.sources.network.session.SessionManager
import com.dimsuz.yamm.login.method_select.LoginMethodSelectController
import com.dimsuz.yamm.login.server_select.ServerSelectController
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.appScope
import com.dimsuz.yamm.util.instance

class MainActivity : BaseControllerActivity() {

  override fun createController(): Controller? {
    val scope = appScope
    val isLoggedIn = scope.instance<SessionManager>().currentUserId != null
    return if (!isLoggedIn) {
      val isServerUrlSet = scope.instance<AppConfig>().getServerUrl() != null
      if (isServerUrlSet)
        LoginMethodSelectController()
      else
        ServerSelectController()
    } else {
      // TODO Messaging controller
      LoginMethodSelectController()
    }
  }
}