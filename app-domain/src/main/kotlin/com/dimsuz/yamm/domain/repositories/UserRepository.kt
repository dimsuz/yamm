package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.User

interface UserRepository {
  fun getById(ids: List<String>): List<User>
  fun insertOrReplace(users: List<User>)
}