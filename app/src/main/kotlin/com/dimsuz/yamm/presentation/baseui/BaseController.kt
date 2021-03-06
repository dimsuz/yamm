package com.dimsuz.yamm.presentation.baseui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*

abstract class BaseController : Controller, LayoutContainer {
  private var bindPropsRootView: View? = null

  constructor()
  constructor(args: Bundle) : super(args)

  final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val rootView = inflater.inflate(getViewLayout(), container, false)
    bindPropsRootView = rootView
    // initialization can happen only after bindPropsRootView is assigned, this is required for view cache to work
    initializeView(rootView)
    return rootView
  }

  @LayoutRes
  abstract fun getViewLayout(): Int
  abstract fun initializeView(rootView: View)
  open protected fun destroyView(view: View) {}

  override val containerView: View? get() = bindPropsRootView

  final override fun onDestroyView(view: View) {
    clearFindViewByIdCache()
    bindPropsRootView = null
    super.onDestroyView(view)
  }
}

