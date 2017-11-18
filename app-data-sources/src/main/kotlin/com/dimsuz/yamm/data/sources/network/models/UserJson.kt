package com.dimsuz.yamm.data.sources.network.models

data class UserJson(
  val id: String,
  val username: String,
  val first_name: String?,
  val last_name: String?,
  val nickname: String?,
  val email: String?)