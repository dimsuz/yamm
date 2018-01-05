package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractorImpl.Request
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractorImpl.Result
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractorImpl.State
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.models.Team
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.domain.repositories.TeamRepository
import com.dimsuz.yamm.domain.util.AppSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

internal class UserChannelsInteractorImpl @Inject constructor(
  private val sessionManager: SessionManager,
  private val teamRepository: TeamRepository,
  private val channelRepository: ChannelRepository,
  schedulers: AppSchedulers,
  private val logger: Logger)
  : ReactiveInteractor<State, Request, Result>(State(), logger, schedulers),
  UserChannelsInteractor {

  private val stateEvents = BehaviorSubject.create<UserChannelsEvent>().toSerialized()

  override fun createCommand(request: Request, state: State): Observable<Result> {
    return when (request) {
      is Request.SetChannelId -> TODO()
      is Request.UpdateUserChannels -> createUpdateChannelsCommand(request)
      is Request.RegisterStateChange -> Observable.empty()
    }
  }

  override fun reduceState(previousState: State, commandResult: Result): State {
    return when (commandResult) {
      is Result.DefaultTeamAvailable -> previousState.copy(userId = commandResult.userId, team = commandResult.team )
    }
  }

  private fun createUpdateChannelsCommand(request: Request.UpdateUserChannels): Observable<Result> {
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

  override fun stateEvents(): Observable<UserChannelsEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  override fun setChannelId(channelId: String) {
    scheduleRequest(Request.SetChannelId(channelId))
  }

  override fun userChannels(): Observable<List<Channel>> {
    val userId = sessionManager.currentUserId ?: throw IllegalStateException("no logged in user found")
    scheduleRequest(Request.UpdateUserChannels(userId))
    return stateChanges
      .filter({ it.userId != null && it.team != null })
      .take(1)
      .flatMap { state -> channelRepository.userChannelsLive(state.userId!!, state.team!!.id) }
      .doOnError { stateEvents.onNext(UserChannelsEvent.LoadFailed(it)) }
  }

  internal data class State(
    val userId: String? = null,
    val team: Team? = null
  )

  internal sealed class Request {
    data class SetChannelId(val channelId: String) : Request()
    data class UpdateUserChannels(val userId: String) : Request()
    data class RegisterStateChange(val state: State) : Request()
  }

  internal sealed class Result {
    data class DefaultTeamAvailable(val userId: String, val team: Team): Result()
  }

}