package com.dimsuz.yamm.repositories.settings

import com.dimsuz.yamm.data.sources.db.persistence.UserPersistence
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.repositories.UserRepository
import com.dimsuz.yamm.repositories.mappers.toDatabaseModel
import io.reactivex.Completable
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
  private val serviceApi: MattermostAuthorizedApi,
  private val userPersistence: UserPersistence) : UserRepository {

  override fun refreshUsers(ids: List<String>): Completable {
    return serviceApi.getUsersByIds(ids)
      .map { usersNetwork -> usersNetwork.map { it.toDatabaseModel() } }
      .flatMapCompletable { usersDatabase ->
        Completable.fromAction {
          userPersistence.replaceUsers(usersDatabase)
        }
      }
  }
}