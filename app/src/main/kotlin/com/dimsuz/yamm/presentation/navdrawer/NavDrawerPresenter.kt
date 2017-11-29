package com.dimsuz.yamm.presentation.navdrawer

import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerItem
import com.dimsuz.yamm.util.doOnNextDebug
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import timber.log.Timber

class NavDrawerPresenter(private val contextManager: NavDrawerContextManager) {
  private var disposable: CompositeDisposable = CompositeDisposable()

  fun attachView(view: NavDrawerView) {
    val itemsDisposable = contextManager.currentContext()
      .doOnNextDebug { Timber.d("switching to new nav drawer context: ${it.type}") }
      .switchMap { context -> context.items }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ items -> view.render(items) }, { Timber.e(it, "failed to obtain user channels")})

    val clicksDisposable =
      Observable.combineLatest(contextManager.currentContext(), view.itemClicks(),
        BiFunction { context: NavDrawerContext, item: NavDrawerItem -> context to item })
        .subscribe(
          { (context, item) ->
            context.selectionObserver.invoke(item)
          },
          {
            Timber.e(it, "error when switching to a new nav drawer item")
          }
        )
    disposable.addAll(itemsDisposable, clicksDisposable)
  }

  fun detachView() {
    disposable.dispose()
  }
}