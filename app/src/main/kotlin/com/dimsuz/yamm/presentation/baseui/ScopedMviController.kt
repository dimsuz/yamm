package com.dimsuz.yamm.presentation.baseui

import android.content.Context
import android.os.Bundle
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

abstract class ScopedMviController<VS, V : MviView<VS>, P: MviPresenter<V, VS>> : BaseMviController<VS, V, P> {

  interface Config : BaseMviController.Config {
    val screenModule: Module
  }

  final override val config: BaseMviController.Config get() = scopedConfig
  protected abstract val scopedConfig: Config

  protected lateinit var screenScope: Scope
    private set

  constructor()
  constructor(args: Bundle) : super(args)

  override fun onContextAvailable(context: Context) {
    super.onContextAvailable(context)
    // initializing scope in onContextAvailable since only at this point can reference
    // appScope without crashing
    screenScope = Toothpick.openScopes(appScope.name, this)
    screenScope.installModules((cachedConfig as Config).screenModule)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(screenScope.name)
  }
}