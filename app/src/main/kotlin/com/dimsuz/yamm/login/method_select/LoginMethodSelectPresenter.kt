package com.dimsuz.yamm.login.method_select

import com.dimsuz.yamm.network.MattermostService
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import timber.log.Timber
import javax.inject.Inject

class LoginMethodSelectPresenter @Inject constructor(private val mattermostService: MattermostService)
  : MviPresenter<LoginMethodSelect.View, LoginMethodSelect.ViewState> {

  override fun attachView(view: LoginMethodSelect.View) {
    Timber.e("Attached view to LoginMethodSelectPresenter, service is $mattermostService")
  }

  override fun detachView(retainInstance: Boolean) {
  }
}