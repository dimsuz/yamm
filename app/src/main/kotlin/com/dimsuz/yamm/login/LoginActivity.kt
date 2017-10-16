package com.dimsuz.yamm.login

import com.bluelinelabs.conductor.Controller
import com.dimsuz.yamm.baseui.BaseControllerActivity
import com.dimsuz.yamm.login.server_select.ServerSelectController

class LoginActivity : BaseControllerActivity() {
  override fun createController(): Controller? {
    return ServerSelectController()
  }
}