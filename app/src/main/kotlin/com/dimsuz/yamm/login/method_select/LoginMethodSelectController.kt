package com.dimsuz.yamm.login.method_select

import android.content.Context
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.BaseMviController
import com.dimsuz.yamm.baseui.util.appScope
import com.dimsuz.yamm.util.instance
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

class LoginMethodSelectController : BaseMviController<LoginMethodSelect.View, LoginMethodSelectPresenter>(), LoginMethodSelect.View {

  private lateinit var screenScope: Scope

  override fun onContextAvailable(context: Context) {
    super.onContextAvailable(context)
    screenScope = Toothpick.openScopes(appScope.name, this)
    screenScope.installModules(object : Module() {
      init {
        bind(LoginMethodSelectPresenter::class.java).to(LoginMethodSelectPresenter::class.java)
      }
    })
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun getViewLayout(): Int {
    return R.layout.login_server_select
  }

  override fun createPresenter(): LoginMethodSelectPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
  }
}
