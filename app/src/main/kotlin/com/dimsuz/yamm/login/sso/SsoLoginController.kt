package com.dimsuz.yamm.login.sso

import android.os.Bundle
import android.webkit.CookieManager
import com.dimsuz.yamm.baseui.util.appScope
import com.dimsuz.yamm.common.EXTRA_SERVER_URL
import com.dimsuz.yamm.common.web_view.WebViewController
import com.dimsuz.yamm.session.SessionManager
import com.dimsuz.yamm.util.instance
import timber.log.Timber

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
      val sessionManager = appScope.instance<SessionManager>()
      Timber.e("cookies: $cookies")
      Timber.e("sessionManager: $sessionManager")
      return true
    }
    return super.onInterceptUrlRedirect(url)
  }
}