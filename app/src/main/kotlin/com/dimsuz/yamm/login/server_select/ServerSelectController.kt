package com.dimsuz.yamm.login.server_select

import android.support.design.widget.TextInputLayout
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.YammApplication
import com.dimsuz.yamm.baseui.BaseController
import com.dimsuz.yamm.baseui.BindView
import com.dimsuz.yamm.baseui.util.appScope
import com.dimsuz.yamm.baseui.util.pushControllerHorizontal
import com.dimsuz.yamm.baseui.util.resourcesUnsafe
import com.dimsuz.yamm.login.method_select.LoginMethodSelectController
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.instance

class ServerSelectController : BaseController() {
  private val serverInputLayout: TextInputLayout by BindView(R.id.server_select_input_layout)
  private val continueButton: View by BindView(R.id.button_continue)

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
    appScope.instance<AppConfig>().setServerUrl(serverUrl.trim().toString())
    appScope.instance<YammApplication>().onServerUrlChanged()
  }

  private fun validateServerUrl(serverUrl: CharSequence): Boolean {
    return serverUrl.isNotBlank() && (serverUrl.startsWith("https://") || serverUrl.startsWith("http://"))
  }

  private fun routeToNextScreen() {
    val controller = LoginMethodSelectController()
    router.pushControllerHorizontal(controller)
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