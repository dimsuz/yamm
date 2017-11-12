package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.UserDbModel

internal class UserPersistenceImpl : UserPersistence {

  override fun getUsersById(ids: List<String>): List<UserDbModel> {
    TODO("not implemented")
  }

  override fun replaceUsers(users: List<UserDbModel>) {
    TODO("not implemented")
  }
}