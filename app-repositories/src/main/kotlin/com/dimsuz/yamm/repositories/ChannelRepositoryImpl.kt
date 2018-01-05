package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.dimsuz.yamm.data.sources.db.persistence.ChannelPersistence
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.domain.repositories.UserRepository
import com.dimsuz.yamm.repositories.mappers.toDatabaseModel
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

internal class ChannelRepositoryImpl @Inject internal constructor(
  private val serviceApi: MattermostAuthorizedApi,
  private val channelPersistence: ChannelPersistence,
  private val userRepository: UserRepository) : ChannelRepository {

  override fun getChannelIdByName(name: String, teamId: String): String? {
    return channelPersistence.getChannelByName(name, teamId)
  }

  override fun getChannelIds(userId: String, teamId: String): List<String> {
    return channelPersistence.getUserChannelIds(userId, teamId)
  }

  override fun getChannelById(channelId: String): Maybe<Channel> {
    // TODO if missing in DB get from network by using serviceApi.getChannel() + cache it
    return Maybe.fromCallable { channelPersistence.getChannelById(channelId)?.toDomainModel() }
  }

  override fun refreshUserChannels(userId: String, teamId: String): Completable {
    return serviceApi.getUserChannels(userId, teamId)
      .map { channelsNetwork -> channelsNetwork.map { it.toDatabaseModel(userId, teamId) } }
      .flatMapCompletable { channelsDatabase ->
        refreshTeamMates(channelsDatabase)
          .andThen(Completable.fromAction {
            channelPersistence.replaceUserChannels(channelsDatabase)
          })
      }
  }

  /**
   * If there are any direct channels in the passed list, this method will ensure
   * that corresponding users are updated and saved to the database for later retrieval
   * (needed when constructing user names for direct channel name)
   */
  private fun refreshTeamMates(channels: List<ChannelDbModel>): Completable {
    val teamMateIds = channels.mapNotNullTo(hashSetOf()) { it.teamMateId }
    return if (teamMateIds.isEmpty()) {
      Completable.complete()
    } else {
      userRepository.refreshUsers(teamMateIds)
        // having an error is not critical here
        .doOnError { Timber.d(it, "failed to update users when fetching a channels list") }
        .onErrorComplete()
    }
  }

  @Suppress("ReplaceGetOrSet")
  override fun userChannelsLive(userId: String, teamId: String): Observable<List<Channel>> {
    return channelPersistence.getUserChannelsLive(userId, teamId)
      .map { dbChannels -> dbChannels.map { it.toDomainModel() } }
  }
}

