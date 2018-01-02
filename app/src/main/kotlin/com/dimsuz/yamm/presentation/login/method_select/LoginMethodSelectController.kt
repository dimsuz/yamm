package com.dimsuz.yamm.presentation.login.method_select

import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.navigation.SCREEN_SSO_LOGIN
import com.dimsuz.yamm.presentation.baseui.ScopedMviController
import com.dimsuz.yamm.presentation.baseui.state_render.StateRenderer
import com.dimsuz.yamm.presentation.baseui.state_render.YammLceStateRenderer
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.instance
import kotlinx.android.synthetic.main.login_method_select.*
import ru.terrakok.cicerone.Router
import toothpick.config.Module

class LoginMethodSelectController
  : ScopedMviController<LoginMethodSelect.ViewState, LoginMethodSelect.View, LoginMethodSelectPresenter>(),
  LoginMethodSelect.View {

  override fun createScopedConfig() = object : Config {
    override val viewLayoutResource = R.layout.login_method_select
    override val screenModule = object : Module() {
      init {
        bind(LoginMethodSelectPresenter::class.java).to(LoginMethodSelectPresenter::class.java)
      }
    }
  }

  override fun createStateRenderHelpers(): List<StateRenderer<LoginMethodSelect.ViewState>> {
    return listOf(YammLceStateRenderer(contentViewId = R.id.scrollView,
      additionalContentViewIds = intArrayOf(R.id.toolbar)))
  }

  override fun createPresenter(): LoginMethodSelectPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
    gitlabLoginButton.setOnClickListener {
      val serverUrl = screenScope.instance<AppConfig>().getServerUrl()!!
      val router = appScope.instance<Router>()
      router.navigateTo(SCREEN_SSO_LOGIN, serverUrl)
    }
  }

  override fun renderViewState(viewState: LoginMethodSelect.ViewState) {
  }
}
