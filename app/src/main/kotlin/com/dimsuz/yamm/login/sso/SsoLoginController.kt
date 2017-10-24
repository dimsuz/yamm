package com.dimsuz.yamm.login.sso

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.webkit.CookieManager
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.util.activityUnsafe
import com.dimsuz.yamm.baseui.util.appScope
import com.dimsuz.yamm.common.EXTRA_SERVER_URL
import com.dimsuz.yamm.common.web_view.WebViewController
import com.dimsuz.yamm.session.SessionManager
import com.dimsuz.yamm.util.instance

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

  override fun onInterceptUrlRedirect(url: String?): Boolean {
    if (url?.contains(completeLink) == true) {
      val cookies = CookieManager.getInstance().getCookie(url)
      val cookiesMap = cookies.split(';')
        .filter { it.contains('=') }
        .map { val (key,value) = it.split('='); key.trim() to value.trim() }
        .toMap()

      val userId = cookiesMap[COOKIE_KEY_AUTH_USER_ID]
      val token = cookiesMap[COOKIE_KEY_AUTH_TOKEN]

      if (userId == null || token == null) {
        onSessionCredentialsError()
      } else {
        onSessionCredentialsObtained(token, userId)
      }
      return true
    }
    return super.onInterceptUrlRedirect(url)
  }

  private fun onSessionCredentialsObtained(token: String, userId: String) {
    val sessionManager = appScope.instance<SessionManager>()
    sessionManager.onNewSessionCreated(token, userId)
  }

  private fun onSessionCredentialsError() {
    AlertDialog.Builder(activityUnsafe)
      .setTitle(R.string.sso_login_failed_title)
      .setMessage(R.string.sso_login_failed_message)
      .setPositiveButton(R.string.action_ok, { _, _ -> router.popCurrentController() })
      .show()
  }
}