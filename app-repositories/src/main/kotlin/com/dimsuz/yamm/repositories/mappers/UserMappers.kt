package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.db.models.UserDbModel
import com.dimsuz.yamm.data.sources.network.models.UserJson
import com.dimsuz.yamm.domain.models.User

internal fun UserJson.toDatabaseModel(): UserDbModel {
  return UserDbModel(id, username, first_name, last_name, nickname, email)
}

internal fun UserDbModel.toDomainModel(): User {
  return User(
    id = this.id,
    username = this.username,
    firstName = this.firstName,
    lastName = this.lastName,
    nickname = this.nickname,
    email = this.email
  )
}