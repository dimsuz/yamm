package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.db.models.UserDbModel
import com.dimsuz.yamm.data.sources.network.models.UserJson

internal fun UserJson.toDatabaseModel(): UserDbModel {
  return UserDbModel(id, username, first_name, last_name, nickname, email)
}