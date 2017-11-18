package com.dimsuz.yamm.domain.repositories

import io.reactivex.Completable

interface UserRepository {
  fun refreshUsers(ids: List<String>): Completable
}