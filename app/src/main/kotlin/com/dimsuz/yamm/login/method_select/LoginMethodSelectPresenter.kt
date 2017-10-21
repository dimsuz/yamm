package com.dimsuz.yamm.login.method_select

import com.dimsuz.yamm.baseui.BaseMviPresenter
import com.dimsuz.yamm.baseui.models.ViewIntentResult
import com.dimsuz.yamm.network.MattermostService
import com.dimsuz.yamm.util.AppSchedulers
import io.reactivex.Observable
import javax.inject.Inject

sealed class ScreenEvent
private object ServerConfigLoading : ScreenEvent()

class LoginMethodSelectPresenter @Inject constructor(private val mattermostService: MattermostService,
                                                     schedulers: AppSchedulers)
  : BaseMviPresenter<LoginMethodSelect.View, LoginMethodSelect.ViewState, ScreenEvent>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenEvent>> {
    return emptyList()
  }

  override fun viewStateReducer(previousState: LoginMethodSelect.ViewState, event: ScreenEvent): ViewIntentResult<LoginMethodSelect.ViewState> {
    return ViewIntentResult(previousState, null)
  }

  override fun createInitialState(): LoginMethodSelect.ViewState {
    return LoginMethodSelect.ViewState()
  }

}