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
import com.dimsuz.yamm.domain.util.AppConfig
import com.dimsuz.yamm.domain.util.AppSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

internal class UserChannelsInteractorImpl @Inject constructor(
  private val sessionManager: SessionManager,
  private val appConfig: AppConfig,
  private val teamRepository: TeamRepository,
  private val channelRepository: ChannelRepository,
  schedulers: AppSchedulers,
  private val logger: Logger)
  : ReactiveInteractor<State, Request, Result>(State(), logger, schedulers),
  UserChannelsInteractor {

  private val stateEvents = BehaviorSubject.create<UserChannelsEvent>().toSerialized()

  override fun createCommand(request: Request, state: State): Observable<Result> {
    return when (request) {
      is Request.SetChannelId -> createSetChannelCommand(request, state)
      is Request.UpdateUserChannels -> createUpdateChannelsCommand(request, state)
    }
  }

  override fun reduceState(previousState: State, commandResult: Result): State {
    return when (commandResult) {
      is Result.DefaultTeamAvailable -> previousState.copy(userId = commandResult.userId, team = commandResult.team )
      is Result.CurrentChannelChanged -> previousState.copy(channel = commandResult.channel)
    }
  }

  private fun createSetChannelCommand(request: Request.SetChannelId, state: State): Observable<Result> {
    return if (state.channel?.id != request.channelId) {
      channelRepository.getChannelById(request.channelId)
        .doOnComplete({ logger.debug("failed to set channel ${request.channelId}") })
        .flatMapObservable<Result> { channel ->
          appConfig.setLastUsedChannelId(state.userId!!, state.team?.id!!, channel.id)
          Observable.just(Result.CurrentChannelChanged(channel))
        }
    } else {
      Observable.empty()
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
          .doOnNext({ updateCurrentChannel(request.userId, defaultTeam.id, state) })
      }
  }

  private fun updateCurrentChannel(userId: String, teamId: String, state: State) {
    if (state.channel == null || (state.team != null && state.team.id != teamId)) {
      val channelId = appConfig.getLastUsedChannelId(userId, teamId)
        ?: channelRepository.getChannelIdByName(DEFAULT_CHANNEL_NAME, teamId)
        ?: channelRepository.getChannelIds(userId, teamId).firstOrNull().also { logNoDefaultChannel() }
        ?: throw IllegalStateException("failed to get default channel: team has no channels")
      setChannelId(channelId)
    }
  }

  override fun stateEvents(): Observable<UserChannelsEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  override fun setChannelId(channelId: String) {
    scheduleRequest(Request.SetChannelId(channelId))
  }

  override fun currentChannel(): Observable<Channel> {
    return stateChanges
      .filter { it.channel != null }
      .map { it.channel!! }
      .distinctUntilChanged()
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

  private fun logNoDefaultChannel() {
    logger.error("team has no default channel, getting a first one available")
  }

  internal data class State(
    val userId: String? = null,
    val team: Team? = null,
    val channel: Channel? = null
  )

  internal sealed class Request {
    data class SetChannelId(val channelId: String) : Request()
    data class UpdateUserChannels(val userId: String) : Request()
  }

  internal sealed class Result {
    data class DefaultTeamAvailable(val userId: String, val team: Team): Result()
    data class CurrentChannelChanged(val channel: Channel): Result()
  }

}