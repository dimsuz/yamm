package com.dimsuz.yamm.navigation

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.dimsuz.yamm.baseui.util.obtainHorizontalTransaction

interface ControllerFactory {
  fun createController(screenKey: String, payload: Any?): Controller

  fun createPushTransaction(screenKey: String, payload: Any?, controller: Controller): RouterTransaction {
    return controller.obtainHorizontalTransaction()
  }

  fun createReplaceTransaction(screenKey: String, payload: Any?, controller: Controller): RouterTransaction {
    return controller.obtainHorizontalTransaction()
  }
}