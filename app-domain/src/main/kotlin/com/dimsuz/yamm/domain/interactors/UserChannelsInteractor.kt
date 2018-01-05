package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.models.Team
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.domain.repositories.TeamRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

sealed class UserChannelsEvent {
  object     Loading : UserChannelsEvent()
  data class LoadFailed(val error: Throwable) : UserChannelsEvent()
  object     Idle : UserChannelsEvent()
}

class UserChannelsInteractor @Inject constructor(
  private val sessionManager: SessionManager,
  private val teamRepository: TeamRepository,
  private val channelRepository: ChannelRepository,
  private val logger: Logger) {

  private sealed class Request {
    data class SetChannelId(val channelId: String) : Request()
    data class UpdateUserChannels(val userId: String) : Request()
    data class RegisterStateChange(val state: State) : Request()
  }

  private sealed class Result {
    data class DefaultTeamAvailable(val userId: String, val team: Team): Result()
  }

  private val stateEvents = BehaviorSubject.create<UserChannelsEvent>().toSerialized()
  private val stateChanges: Observable<State>
  private val requestStream = BehaviorSubject.createDefault<Request>(Request.RegisterStateChange(createInitialState()))

  init {
    // TODO document stuff with RegisterStateChange
    stateChanges = requestStream
      .filter { it !is Request.RegisterStateChange }
      .withLatestFrom(requestStream.ofType(Request.RegisterStateChange::class.java),
        BiFunction { request: Request, stateChange: Request.RegisterStateChange -> request to stateChange.state})
      .flatMap { (request, state) -> createCommand(request, state) }
      .scan(createInitialState(), { s, r -> reduceState(s, r) })
      .share()

    stateChanges
      .subscribe(
        {
          logger.debug("state updated to: $it")
          requestStream.onNext(Request.RegisterStateChange(it))
        },
        { logger.error(it, "commands must not throw errors, rather set some error flag in state") })
  }

  private fun reduceState(previousState: State, result: Result): State {
    return when (result) {
      is Result.DefaultTeamAvailable -> previousState.copy(userId = result.userId, team = result.team )
    }
  }

  // TODO document that Observable can emit no result - that's ok, means no command needed for this request
  private fun createCommand(request: Request, state: State): Observable<Result> {
    return when (request) {
      is Request.SetChannelId -> TODO()
      is Request.UpdateUserChannels -> createUpdateChannelsCommand(request, state)
      is Request.RegisterStateChange -> Observable.empty()
    }
  }

  private fun createUpdateChannelsCommand(request: Request.UpdateUserChannels, state: State): Observable<Result> {
    return Completable.fromAction { stateEvents.onNext(UserChannelsEvent.Loading) }
      .andThen(teamRepository.userTeams(request.userId))
      .flatMapObservable { teams ->
        val defaultTeam = teams.firstOrNull() ?: throw IllegalStateException("user $request.userId has no teams")
        channelRepository.refreshUserChannels(request.userId, defaultTeam.id)
          .andThen(Completable.fromAction { stateEvents.onNext(UserChannelsEvent.Idle) })
          .onErrorResumeNext { e -> Completable.fromAction { stateEvents.onNext(UserChannelsEvent.LoadFailed(e)) } }
          .andThen(Observable.just(Result.DefaultTeamAvailable(request.userId, defaultTeam)))
      }
  }

  fun stateEvents(): Observable<UserChannelsEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  fun setChannelId(channelId: String) {
    requestStream.onNext(Request.SetChannelId(channelId))
  }

  fun userChannels(): Observable<List<Channel>> {
    val userId = sessionManager.currentUserId ?: throw IllegalStateException("no logged in user found")
    requestStream.onNext(Request.UpdateUserChannels(userId))
    return stateChanges
      .filter({ it.userId != null && it.team != null })
      .take(1)
      .flatMap { state ->
        channelRepository.userChannelsLive(state.userId!!, state.team!!.id)
      }
      .doOnError { stateEvents.onNext(UserChannelsEvent.LoadFailed(it)) }
  }

  private data class State(
    val userId: String?,
    val team: Team?
  )

  private fun createInitialState(): State {
    return UserChannelsInteractor.State(userId = null, team = null)
  }
}