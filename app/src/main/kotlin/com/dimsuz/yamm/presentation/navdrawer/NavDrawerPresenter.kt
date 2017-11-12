package com.dimsuz.yamm.presentation.navdrawer

import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.doOnNextDebug
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber

class NavDrawerPresenter(private val contextManager: NavDrawerContextManager) {
  private var itemsDisposable: Disposable? = null

  fun attachView(view: NavDrawerView) {
    itemsDisposable = contextManager.currentContext()
      .doOnNextDebug { Timber.d("switching to new nav drawer context: ${it.type}") }
      .switchMap { context -> context.items }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ items -> view.render(items) }, { Timber.e(it, "failed to obtain user channels")})
  }

  fun detachView() {
    itemsDisposable?.dispose()
  }
}