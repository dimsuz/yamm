package com.dimsuz.yamm.login.server_select

import android.support.design.widget.TextInputLayout
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.BaseController
import com.dimsuz.yamm.baseui.BindView
import com.dimsuz.yamm.baseui.bindView
import com.dimsuz.yamm.baseui.util.resourcesUnsafe

class ServerSelectController : BaseController() {
  private val serverInputLayout: TextInputLayout by BindView(R.id.server_select_input_layout)

  override fun getViewLayout(): Int {
    return R.layout.login_server_select
  }

  override fun initializeView(rootView: View) {
    serverInputLayout.error = resourcesUnsafe.getString(R.string.login_server_select_helper_text)
  }

}