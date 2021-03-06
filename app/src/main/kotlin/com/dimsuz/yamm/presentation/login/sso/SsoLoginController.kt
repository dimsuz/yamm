package com.dimsuz.yamm.presentation.login.sso

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.webkit.CookieManager
import com.dimsuz.yamm.R
import com.dimsuz.yamm.navigation.SCREEN_MESSAGE_LIST
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.common.EXTRA_SERVER_URL
import com.dimsuz.yamm.presentation.common.web_view.WebViewController
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.util.instance
import ru.terrakok.cicerone.Router

private const val GITLAB_SSO_LOGIN_LINK = "%s/oauth/gitlab/mobile_login"
private const val GITLAB_SSO_LOGIN_COMPLETE_LINK = "%s/signup/gitlab/complete"
private const val COOKIE_KEY_AUTH_TOKEN = "MMAUTHTOKEN"
private const val COOKIE_KEY_AUTH_USER_ID = "MMUSERID"

class SsoLoginController(args: Bundle) : WebViewController(args) {

  companion object {
    fun create(serverUrl: String): WebViewController {
      val args = Bundle()
      args.putString(EXTRA_SERVER_URL, serverUrl)
      args.putAll(WebViewController.createArgs(GITLAB_SSO_LOGIN_LINK.format(serverUrl)))
      return SsoLoginController(args)
    }
  }

  private val serverUrl get() = args.getString(EXTRA_SERVER_URL)
  private val completeLink: String = GITLAB_SSO_LOGIN_COMPLETE_LINK.format(serverUrl)

  override fun onPageFinished(url: String?) {
    if (url?.contains(completeLink) == true) {
      val cookies = CookieManager.getInstance().getCookie(serverUrl)
      val cookiesMap = cookies.split(';')
        .filter { it.contains('=') }
        .map { val (key,value) = it.split('='); key.trim() to value.trim() }
        .toMap()

      val userId = cookiesMap[COOKIE_KEY_AUTH_USER_ID]
      val token = cookiesMap[COOKIE_KEY_AUTH_TOKEN]

      if (userId != null && token != null) {
        onSessionCredentialsObtained(token, userId)
      } else {
        onSessionCredentialsError()
      }
    }
  }

  private fun onSessionCredentialsObtained(token: String, userId: String) {
    val sessionManager = appScope.instance<SessionManager>()
    sessionManager.onNewSessionCreated(token, userId)
    val router = appScope.instance<Router>()
    router.newRootScreen(SCREEN_MESSAGE_LIST)
  }

  private fun onSessionCredentialsError() {
    AlertDialog.Builder(activityUnsafe)
      .setTitle(R.string.sso_login_failed_title)
      .setMessage(R.string.sso_login_failed_message)
      .setPositiveButton(R.string.action_ok, { _, _ -> router.popCurrentController() })
      .show()
  }
}