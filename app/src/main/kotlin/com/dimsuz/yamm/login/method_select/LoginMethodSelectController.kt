package com.dimsuz.yamm.login.method_select

import android.content.Context
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.baseui.BaseMviController
import com.dimsuz.yamm.baseui.BindView
import com.dimsuz.yamm.baseui.state_render.StateRenderer
import com.dimsuz.yamm.baseui.state_render.YammLceStateRenderer
import com.dimsuz.yamm.baseui.util.appScope
import com.dimsuz.yamm.baseui.util.pushControllerHorizontal
import com.dimsuz.yamm.login.sso.SsoLoginController
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.instance
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

class LoginMethodSelectController : BaseMviController<LoginMethodSelect.ViewState, LoginMethodSelect.View, LoginMethodSelectPresenter>(), LoginMethodSelect.View {

  private val buttonGitlabLogin: View by BindView(R.id.login_with_gitlab)

  override val config: Config get() = object : Config {
    override val viewLayoutResource: Int = R.layout.login_method_select
  }

  override fun createStateRenderHelpers(): List<StateRenderer<LoginMethodSelect.ViewState>> {
    return listOf(YammLceStateRenderer(contentViewId = R.id.scroll_view, additionalContentViewIds = intArrayOf(R.id.toolbar)))
  }

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

  override fun createPresenter(): LoginMethodSelectPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
    buttonGitlabLogin.setOnClickListener {
      val serverUrl = screenScope.instance<AppConfig>().getServerUrl()!!
      router.pushControllerHorizontal(SsoLoginController.create(serverUrl))
    }
  }

  override fun renderViewState(viewState: LoginMethodSelect.ViewState) {
  }
}
