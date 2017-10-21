package com.dimsuz.yamm.login.method_select

import com.dimsuz.yamm.baseui.BaseMviPresenter
import com.dimsuz.yamm.baseui.RoutingAction
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

  override fun viewStateReducer(previousState: LoginMethodSelect.ViewState, event: ScreenEvent): LoginMethodSelect.ViewState {
    return previousState
  }

  override fun routingStateReducer(previousState: LoginMethodSelect.ViewState, newState: LoginMethodSelect.ViewState, event: ScreenEvent): RoutingAction? {
    return null
  }

  override fun createInitialState(): LoginMethodSelect.ViewState {
    return LoginMethodSelect.ViewState()
  }

}