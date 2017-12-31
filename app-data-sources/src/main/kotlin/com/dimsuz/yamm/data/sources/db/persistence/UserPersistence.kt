package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.UserDbModel

interface UserPersistence {
  fun getUserById(id: String): UserDbModel?
  fun getUsersById(ids: List<String>): List<UserDbModel>
  fun findNonExistingIds(ids: List<String>): List<String>
  fun replaceUsers(users: List<UserDbModel>)
}