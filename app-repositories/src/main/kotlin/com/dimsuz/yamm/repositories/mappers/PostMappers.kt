package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.db.models.PostDbModel
import com.dimsuz.yamm.data.sources.network.models.PostJson
import com.dimsuz.yamm.domain.models.Post

fun PostJson.toDatabaseModel(): PostDbModel {
  TODO()
}

fun PostDbModel.toDomainModel(): Post {
  TODO()
}