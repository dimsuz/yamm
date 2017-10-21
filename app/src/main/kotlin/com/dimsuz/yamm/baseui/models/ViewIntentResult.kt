package com.dimsuz.yamm.baseui.models

/**
 * Represents an action to be performed on a view.
 * It can be only a view state which will result in a call to render() or
 * a view state accompanied by navigation action which will be usually performed
 * after a view state is rendered
 */
data class ViewIntentResult<out VS>(
  val viewState: VS,
  val routingAction: RoutingAction?
)

typealias RoutingAction = () -> Unit
