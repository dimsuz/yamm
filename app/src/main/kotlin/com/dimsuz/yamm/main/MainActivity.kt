package com.dimsuz.yamm.main

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.dimsuz.yamm.baseui.BaseControllerActivity
import com.dimsuz.yamm.data.sources.network.session.SessionManager
import com.dimsuz.yamm.login.method_select.LoginMethodSelectController
import com.dimsuz.yamm.login.server_select.ServerSelectController
import com.dimsuz.yamm.login.sso.SsoLoginController
import com.dimsuz.yamm.navigation.ControllerFactory
import com.dimsuz.yamm.navigation.ControllerNavigator
import com.dimsuz.yamm.navigation.payloadAsParam
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.appScope
import com.dimsuz.yamm.util.instance
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import toothpick.Toothpick
import javax.inject.Inject

class MainActivity : BaseControllerActivity(), ControllerFactory {
  @Inject lateinit var navigatorHolder: NavigatorHolder
  lateinit var navigator: Navigator

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

  override fun createController(screenKey: String, payload: Any?): Controller {
    return when (screenKey) {
      SCREEN_SERVER_SELECT -> ServerSelectController()
      SCREEN_LOGIN_METHOD_SELECT -> LoginMethodSelectController()
      SCREEN_SSO_LOGIN -> SsoLoginController.create(payloadAsParam(payload, "serverUrl"))
      SCREEN_MESSAGE_LIST -> TODO()
      else -> throw RuntimeException("don't know how to create screen $screenKey")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, appScope)
    navigator = ControllerNavigator(conductorRouter, this)
  }

  override fun onResume() {
    super.onResume()
    navigatorHolder.setNavigator(navigator)
  }

  override fun onPause() {
    super.onPause()
    navigatorHolder.removeNavigator()
  }
}