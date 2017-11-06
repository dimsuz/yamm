package com.dimsuz.yamm.presentation.baseui

interface HasLceState<out E, S : HasLceState<E, S>> : HasTransientState<S> {
  val showProgressBar: Boolean
  val contentLoadingError: E?
}

interface HasTransientState<out S : HasTransientState<S>> {
  fun clearTransientState(): S
}
