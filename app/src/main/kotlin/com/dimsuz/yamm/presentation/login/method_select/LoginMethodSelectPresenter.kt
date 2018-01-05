package com.dimsuz.yamm.presentation.login.method_select

import com.dimsuz.yamm.domain.models.ServerConfig
import com.dimsuz.yamm.domain.repositories.ServerConfigRepository
import com.dimsuz.yamm.presentation.baseui.BaseMviPresenter
import com.dimsuz.yamm.presentation.baseui.RoutingAction
import com.dimsuz.yamm.domain.util.AppSchedulers
import com.dimsuz.yamm.util.ErrorDetailsExtractor
import io.reactivex.Observable
import javax.inject.Inject

sealed class ScreenEvent
private object ServerConfigLoading : ScreenEvent()
private data class ServerConfigLoaded(val serverConfig: ServerConfig) : ScreenEvent()
private data class ServerConfigLoadFailed(val error: Throwable) : ScreenEvent()

class LoginMethodSelectPresenter @Inject constructor(private val serverConfigRepository: ServerConfigRepository,
                                                     private val errorDetailsExtractor: ErrorDetailsExtractor,
                                                     schedulers: AppSchedulers)
  : BaseMviPresenter<LoginMethodSelect.View, LoginMethodSelect.ViewState, ScreenEvent>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenEvent>> {
    val fetchServerConfigFlow = intent { fetchServerConfigFlow() }
    return listOf(fetchServerConfigFlow)
  }

  private fun fetchServerConfigFlow(): Observable<ScreenEvent> {
    return serverConfigRepository.getServerConfig()
      .map<ScreenEvent> { ServerConfigLoaded(it) }
      .onErrorReturn { ServerConfigLoadFailed(it) }
      .startWith(ServerConfigLoading)
  }

  override fun viewStateReducer(previousState: LoginMethodSelect.ViewState, event: ScreenEvent): LoginMethodSelect.ViewState {
    val nextStateDraft = previousState.clearTransientState()
    return when (event) {
      is ServerConfigLoading -> nextStateDraft.copy(showProgressBar = true)
      is ServerConfigLoaded -> nextStateDraft.copy(
        showGitlabSignIn = event.serverConfig.isGitlabSignInEnabled,
        showEmailSignIn = event.serverConfig.isEmailSignInEnabled)
      is ServerConfigLoadFailed -> nextStateDraft.copy(contentLoadingError = errorDetailsExtractor.extractErrorText(event.error))
    }
  }

  override fun routingStateReducer(previousState: LoginMethodSelect.ViewState, newState: LoginMethodSelect.ViewState, event: ScreenEvent): RoutingAction? {
    return null
  }

  override fun createInitialState(): LoginMethodSelect.ViewState {
    return LoginMethodSelect.ViewState(
      showProgressBar = false,
      contentLoadingError = null,
      showEmailSignIn = false,
      showGitlabSignIn = false
    )
  }

}