package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.models.Team
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.domain.repositories.SessionManager
import com.dimsuz.yamm.domain.repositories.TeamRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

sealed class UserChannelsEvent {
  object     Loading : UserChannelsEvent()
  data class DefaultTeamLoaded(val userId: String, val team: Team): UserChannelsEvent()
  data class LoadFailed(val error: Throwable) : UserChannelsEvent()
  object     Idle : UserChannelsEvent()
}

class UserChannelsInteractor @Inject constructor(
  private val sessionManager: SessionManager,
  private val teamRepository: TeamRepository,
  private val channelRepository: ChannelRepository,
  private val logger: Logger) {

  private val stateEvents = BehaviorSubject.create<UserChannelsEvent>().toSerialized()

  fun stateEvents(): Observable<UserChannelsEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  fun userChannels(): Observable<List<Channel>> {
    return Completable
      .fromAction { updateUserChannels() }
      .andThen(
        stateEvents
          .filter { it is UserChannelsEvent.DefaultTeamLoaded }
          .take(1)
          .flatMap { event ->
            val defaultTeamEvent = event as UserChannelsEvent.DefaultTeamLoaded
            channelRepository.userChannelsLive(defaultTeamEvent.userId, defaultTeamEvent.team.id)
          }
          .doOnError { stateEvents.onNext(UserChannelsEvent.LoadFailed(it)) })
  }

  private fun updateUserChannels() {
    val userId = sessionManager.currentUserId ?: throw IllegalStateException("no logged in user found")
    stateEvents.onNext(UserChannelsEvent.Loading)
    teamRepository
      .userTeams(userId)
      .map { teams ->
        teams.firstOrNull() ?: throw IllegalStateException("user $userId has no teams")
      }
      .doOnSuccess { defaultTeam ->
        stateEvents.onNext(UserChannelsEvent.DefaultTeamLoaded(userId, defaultTeam))
      }
      .flatMapCompletable { defaultTeam ->
        channelRepository.refreshUserChannels(userId, defaultTeam.id)
      }
      .subscribe(
        { stateEvents.onNext(UserChannelsEvent.Idle) },
        { stateEvents.onNext(UserChannelsEvent.LoadFailed(it)) })
  }

}