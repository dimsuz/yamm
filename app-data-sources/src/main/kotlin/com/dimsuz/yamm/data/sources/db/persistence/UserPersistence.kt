package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.UserDbModel

interface UserPersistence {
  fun getUsersById(ids: List<String>): List<UserDbModel>
  fun replaceUsers(users: List<UserDbModel>)
}