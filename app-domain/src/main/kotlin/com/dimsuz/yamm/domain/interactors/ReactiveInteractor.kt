package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.core.util.checkMainThread
import com.dimsuz.yamm.domain.util.AppSchedulers
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

internal abstract class ReactiveInteractor<StateType, in RequestType, ResultType>(
  initialState: StateType,
  logger: Logger,
  protected val schedulers: AppSchedulers) {

  protected val stateChanges: Observable<StateType>
  private val requestStream = PublishSubject.create<RequestType>()
  // TODO document why it is private and shouldn't leak
  @Volatile
  private var lastState: StateType = initialState

  init {
    checkMainThread()
    stateChanges = requestStream
      .flatMap { request -> createCommand(request, lastState) }
      .scan(initialState, { s, r -> reduceState(s, r) })
      .share()

    stateChanges
      .observeOn(schedulers.ui)
      .subscribe(
        {
          logger.debug("state updated to: $it")
          lastState = it
        },
        { logger.error(it, "commands must not throw errors, rather set some error flag in state") })
  }

  protected fun scheduleRequest(request: RequestType) {
    checkMainThread()
    requestStream.onNext(request)
  }

  // TODO document that Observable can emit no result - that's ok, means no command needed for this request
  abstract fun createCommand(request: RequestType, state: StateType): Observable<ResultType>
  abstract fun reduceState(previousState: StateType, commandResult: ResultType): StateType
}