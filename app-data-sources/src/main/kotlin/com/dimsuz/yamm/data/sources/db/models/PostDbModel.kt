package com.dimsuz.yamm.data.sources.db.models

data class PostDbModel(
  val id: String,
  val userId: String,
  val channelId: String,
  val message: String,
  val type: String
)