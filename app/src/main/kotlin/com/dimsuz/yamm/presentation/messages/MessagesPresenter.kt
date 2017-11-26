package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.presentation.baseui.BaseMviPresenter
import com.dimsuz.yamm.presentation.baseui.RoutingAction
import com.dimsuz.yamm.presentation.login.method_select.ScreenEvent
import com.dimsuz.yamm.util.AppSchedulers
import io.reactivex.Observable
import javax.inject.Inject

class MessagesPresenter @Inject constructor(
  schedulers: AppSchedulers
) : BaseMviPresenter<Messages.View, Messages.ViewState, ScreenEvent>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenEvent>> {
    TODO("not implemented")
  }

  override fun viewStateReducer(previousState: Messages.ViewState, event: ScreenEvent): Messages.ViewState {
    TODO("not implemented")
  }

  override fun routingStateReducer(previousState: Messages.ViewState, newState: Messages.ViewState, event: ScreenEvent): RoutingAction? {
    TODO("not implemented")
  }

  override fun createInitialState(): Messages.ViewState {
    TODO("not implemented")
  }
}