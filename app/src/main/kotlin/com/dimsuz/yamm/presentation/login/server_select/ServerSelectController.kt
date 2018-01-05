package com.dimsuz.yamm.presentation.login.server_select

import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.YammApplication
import com.dimsuz.yamm.domain.util.AppConfig
import com.dimsuz.yamm.navigation.SCREEN_LOGIN_METHOD_SELECT
import com.dimsuz.yamm.presentation.baseui.BaseController
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.baseui.util.resourcesUnsafe
import com.dimsuz.yamm.util.instance
import kotlinx.android.synthetic.main.login_server_select.*
import ru.terrakok.cicerone.Router

class ServerSelectController : BaseController() {
  override fun getViewLayout(): Int {
    return R.layout.login_server_select
  }

  override fun initializeView(rootView: View) {
    resetValidationError()
    val serverUrl = appScope.instance<AppConfig>().getServerUrl()
    if (serverUrl != null) {
      serverInputLayout.editText!!.setText(serverUrl)
      serverInputLayout.editText!!.setSelection(serverUrl.length)
    }
    continueButton.setOnClickListener { onContinueButtonClicked() }
  }

  private fun onContinueButtonClicked() {
    val serverUrl = serverInputLayout.editText!!.text.trim()
    if (validateServerUrl(serverUrl)) {
      resetValidationError()
      saveServerUrl(serverUrl)
      routeToNextScreen()
    } else {
      showValidationError()
    }
  }

  private fun saveServerUrl(serverUrl: CharSequence) {
    val appConfig = appScope.instance<AppConfig>()
    appConfig.setServerUrl(serverUrl.trim().toString())
    if (serverUrl != appConfig.getServerUrl()) {
      appScope.instance<YammApplication>().onServerUrlChanged()
    }
  }

  private fun validateServerUrl(serverUrl: CharSequence): Boolean {
    return serverUrl.isNotBlank() && (serverUrl.startsWith("https://") || serverUrl.startsWith("http://"))
  }

  private fun routeToNextScreen() {
    val router = appScope.instance<Router>()
    router.navigateTo(SCREEN_LOGIN_METHOD_SELECT)
  }

  private fun showValidationError() {
    serverInputLayout.setErrorTextAppearance(R.style.TextAppearance_Design_Error)
    serverInputLayout.error = resourcesUnsafe.getString(R.string.login_server_select_error_invalid_url)
  }

  private fun resetValidationError() {
    serverInputLayout.setErrorTextAppearance(R.style.TextAppearance_Yamm_HelperText)
    serverInputLayout.error = resourcesUnsafe.getString(R.string.login_server_select_helper_text)
  }

}