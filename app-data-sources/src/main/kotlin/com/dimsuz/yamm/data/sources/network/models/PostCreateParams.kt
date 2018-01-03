package com.dimsuz.yamm.data.sources.network.models

data class PostCreateParams(
  val channel_id: String,
  val message: String,
  val root_id: String?,
  val file_ids: List<String>?,
  val props: String?
)