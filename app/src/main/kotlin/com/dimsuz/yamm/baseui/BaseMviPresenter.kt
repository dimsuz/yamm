package com.dimsuz.yamm.baseui

import com.dimsuz.yamm.util.AppSchedulers
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

typealias RoutingAction = () -> Unit

abstract class BaseMviPresenter<V : MviView<VS>, VS, EV>(private val schedulers: AppSchedulers,
                                                         private val skipRenderOfInitialState: Boolean = false) : MviBasePresenter<V, VS>() {
  final override fun bindIntents() {
    val stateChanges = Observable.merge(createIntents())
      .scan(ViewIntentResult(createInitialState(), null), { s, ev ->
        val ns = viewStateReducer(s.viewState, ev)
        val rs = routingStateReducer(s.viewState, ns, ev)
        ViewIntentResult(ns, rs)
      })
      .skipFirstIf(skipRenderOfInitialState)
      .observeOn(schedulers.ui)
      .doAfterNext { it.routingAction?.invoke() }
      .map { it.viewState }

    subscribeViewState(stateChanges, { view, viewState -> view.render(viewState) })
  }

  protected abstract fun createIntents(): List<Observable<out EV>>
  protected abstract fun createInitialState(): VS

  abstract fun viewStateReducer(previousState: VS, event: EV): VS
  abstract fun routingStateReducer(previousState: VS, newState: VS, event: EV): RoutingAction?
}

private fun <T> Observable<T>.skipFirstIf(value: Boolean): Observable<T> {
  return if (value) this.skip(1) else this
}

/**
 * Represents an action to be performed on a view.
 * It can be only a view state which will result in a call to render() or
 * a view state accompanied by navigation action which will be usually performed
 * after a view state is rendered
 */
private data class ViewIntentResult<out VS>(
  val viewState: VS,
  val routingAction: RoutingAction?
)
