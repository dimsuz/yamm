package com.dimsuz.yamm.baseui

import com.dimsuz.yamm.baseui.models.ViewIntentResult
import com.dimsuz.yamm.util.AppSchedulers
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

abstract class BaseMviPresenter<V : MviView<VS>, VS, EV>(private val schedulers: AppSchedulers,
                                                         private val skipRenderOfInitialState: Boolean = false) : MviBasePresenter<V, VS>() {
  final override fun bindIntents() {
    val stateChanges = Observable.merge(createIntents())
      .scan(ViewIntentResult(createInitialState(), null), { s1, s2 -> viewStateReducer(s1.viewState, s2) })
      .skipFirstIf(skipRenderOfInitialState)
      .observeOn(schedulers.ui)
      .doAfterNext { it.routingAction?.invoke() }
      .map { it.viewState }

    subscribeViewState(stateChanges, { view, viewState -> view.render(viewState) })
  }

  protected abstract fun createIntents(): List<Observable<out EV>>
  protected abstract fun createInitialState(): VS

  abstract fun viewStateReducer(previousState: VS, event: EV): ViewIntentResult<VS>
}

private fun <T> Observable<T>.skipFirstIf(value: Boolean): Observable<T> {
  return if (value) this.skip(1) else this
}
