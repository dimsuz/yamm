package com.dimsuz.yamm.baseui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.MviController
import com.hannesdorfmann.mosby3.mvi.MviPresenter

abstract class BaseMviController<VS, V : MviView<VS>, P: MviPresenter<V, VS>> : MviController<V, P>, MviView<VS>, ResettableSupport {

  /**
   * A base interface for configuring BaseMviController.
   * Abstract subclasses may wish to extend this config interface by adding some fields to it
   */
  interface Config {
    val viewLayoutResource: Int
    val clearPreviousStateOnDestroy: Boolean get() = true
  }

  protected abstract val config: Config
  private            val cachedConfig: Config by lazy(LazyThreadSafetyMode.NONE, { config })
  protected          var previousViewState: VS? = null

  final override     val refManager = ResettableReferencesManager()
  final override     var bindPropsRootView: View? = null

  constructor()
  constructor(args: Bundle) : super(args)

  final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val rootView = inflater.inflate(cachedConfig.viewLayoutResource, container, false)
    bindPropsRootView = rootView
    // initialization can happen only after bindPropsRootView is assigned, this is required for BindView delegate to work
    initializeView(rootView)
    return rootView
  }

  abstract fun initializeView(rootView: View)

  override fun onDestroyView(view: View) {
    refManager.reset()
    super.onDestroyView(view)
    if (cachedConfig.clearPreviousStateOnDestroy) {
      previousViewState = null
    }
  }

  final override fun render(viewState: VS) {
    if (previousViewState == null || previousViewState != viewState) {
      renderViewState(viewState)
    }
    previousViewState = viewState
  }

  abstract fun renderViewState(viewState: VS)
}