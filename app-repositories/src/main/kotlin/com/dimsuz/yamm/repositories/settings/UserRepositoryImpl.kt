package com.dimsuz.yamm.repositories.settings

import com.dimsuz.yamm.data.sources.db.persistence.UserPersistence
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.User
import com.dimsuz.yamm.domain.repositories.UserRepository
import com.dimsuz.yamm.repositories.mappers.toDatabaseModel
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
  private val serviceApi: MattermostAuthorizedApi,
  private val userPersistence: UserPersistence) : UserRepository {

  override fun getUser(id: String): Maybe<User> {
    // TODO use a more fine-grained caching mechanism to update even existing users if data is old enough
    return Maybe
      .fromCallable { userPersistence.getUserById(id) }
      .switchIfEmpty(Maybe.defer {
        Timber.d("user $id missing from DB, fetching")
        fetchAndSaveUsers(listOf(id))
          .andThen(Maybe.fromCallable { userPersistence.getUserById(id) })
      })
      .map { it.toDomainModel() }
  }

  override fun getUsers(ids: Set<String>): Single<List<User>> {
    if (ids.isEmpty()) return Single.just(emptyList())
    return refreshUsers(ids)
      .andThen(Single.fromCallable { userPersistence.getUsersById(ids) })
      .map { usersDb -> usersDb.map { it.toDomainModel() } }
  }

  override fun refreshUsers(ids: Set<String>): Completable {
    if (ids.isEmpty()) return Completable.complete()
    // TODO use a more fine-grained caching mechanism to update even existing users if data is old enough
    return Completable
      .defer {
        val missingIds = userPersistence.findNonExistingIds(ids)
        if (missingIds.isNotEmpty()) {
          Timber.d("requested to get ${ids.size} users, " +
            "DB contains ${ids.size - missingIds.size} of them, fetching the rest...")
          fetchAndSaveUsers(missingIds)
        } else {
          Completable.complete()
        }
      }
  }

  private fun fetchAndSaveUsers(ids: List<String>): Completable {
    return serviceApi.getUsersByIds(ids)
      .map { usersNetwork -> usersNetwork.map { it.toDatabaseModel() } }
      .flatMapCompletable { usersDatabase ->
        Completable.fromAction {
          userPersistence.replaceUsers(usersDatabase)
        }
      }
  }
}