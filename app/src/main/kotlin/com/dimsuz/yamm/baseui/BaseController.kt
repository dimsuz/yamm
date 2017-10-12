package com.dimsuz.yamm.baseui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller

abstract class BaseController : Controller, ResettableSupport {
  final override val refManager = ResettableReferencesManager()
  final override var bindPropsRootView: View? = null

  constructor()
  constructor(args: Bundle) : super(args)

  final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val rootView = inflater.inflate(getViewLayout(), container, false)
    bindPropsRootView = rootView
    // initialization can happen only after bindPropsRootView is assigned, this is required for BindView delegate to work
    initializeView(rootView)
    return rootView
  }

  @LayoutRes
  abstract fun getViewLayout(): Int
  abstract fun initializeView(rootView: View)

  override fun onDestroyView(view: View) {
    refManager.reset()
    super.onDestroyView(view)
  }
}

