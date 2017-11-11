package com.dimsuz.yamm.presentation

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.dimsuz.yamm.navigation.ControllerFactory
import com.dimsuz.yamm.navigation.ControllerNavigator
import com.dimsuz.yamm.navigation.SCREEN_LOGIN_METHOD_SELECT
import com.dimsuz.yamm.navigation.SCREEN_MESSAGE_LIST
import com.dimsuz.yamm.navigation.SCREEN_SERVER_SELECT
import com.dimsuz.yamm.navigation.SCREEN_SSO_LOGIN
import com.dimsuz.yamm.navigation.payloadAsParam
import com.dimsuz.yamm.presentation.baseui.BaseControllerActivity
import com.dimsuz.yamm.presentation.login.method_select.LoginMethodSelectController
import com.dimsuz.yamm.presentation.login.server_select.ServerSelectController
import com.dimsuz.yamm.presentation.login.sso.SsoLoginController
import com.dimsuz.yamm.presentation.messages.MessagesController
import com.dimsuz.yamm.presentation.navdrawer.NavDrawerView
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.appScope
import com.dimsuz.yamm.util.instance
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import toothpick.Toothpick
import javax.inject.Inject

class MainActivity : BaseControllerActivity(), ControllerFactory {
  @Inject lateinit var navigatorHolder: NavigatorHolder
  private lateinit var navigator: Navigator
  private lateinit var navDrawerView: NavDrawerView

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
      MessagesController()
    }
  }

  override fun createController(screenKey: String, payload: Any?): Controller {
    return when (screenKey) {
      SCREEN_SERVER_SELECT -> ServerSelectController()
      SCREEN_LOGIN_METHOD_SELECT -> LoginMethodSelectController()
      SCREEN_SSO_LOGIN -> SsoLoginController.create(payloadAsParam(payload, "serverUrl"))
      SCREEN_MESSAGE_LIST -> MessagesController()
      else -> throw RuntimeException("don't know how to create screen $screenKey")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, appScope)
    navigator = ControllerNavigator(conductorRouter, this)
    setupNavigationDrawer()
  }

  private fun setupNavigationDrawer() {
    navDrawerView = NavDrawerView.create(this, contextManager = appScope.instance())
  }

  override fun onResume() {
    super.onResume()
    navigatorHolder.setNavigator(navigator)
  }

  override fun onPause() {
    super.onPause()
    navigatorHolder.removeNavigator()
  }

  override fun onDestroy() {
    super.onDestroy()
    navDrawerView.cleanup()
  }
}