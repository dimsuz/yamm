package com.dimsuz.yamm.login.method_select

import android.content.Context
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.BaseMviController
import com.dimsuz.yamm.baseui.BindView
import com.dimsuz.yamm.baseui.util.appScope
import com.dimsuz.yamm.util.instance
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

class LoginMethodSelectController : BaseMviController<LoginMethodSelect.ViewState, LoginMethodSelect.View, LoginMethodSelectPresenter>(), LoginMethodSelect.View {

  override val config: Config get() = object : Config {
    override val viewLayoutResource: Int = R.layout.login_method_select
  }

  private lateinit var screenScope: Scope
  private val progressBar: View by BindView(R.id.progress_bar)

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

  override fun createPresenter(): LoginMethodSelectPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
  }

  override fun renderViewState(viewState: LoginMethodSelect.ViewState) {
  }
}
