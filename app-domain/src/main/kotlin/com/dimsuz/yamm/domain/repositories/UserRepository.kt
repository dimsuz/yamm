package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface UserRepository {
  fun getUser(id: String): Maybe<User>
  fun getUsers(ids: List<String>): Single<List<User>>

  fun refreshUsers(ids: List<String>): Completable
}