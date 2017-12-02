package com.dimsuz.yamm.data.sources.network.models

data class PostJson(
  val id: String?,
  val user_id: String?,
  val channel_id: String?,
  val message: String?,
  val type: String?,
  val create_at: Long?,
  val update_at: Long?,
  val delete_at: Long?
)