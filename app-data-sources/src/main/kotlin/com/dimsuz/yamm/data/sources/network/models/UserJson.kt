package com.dimsuz.yamm.data.sources.network.models

data class UserJson(
  val id: String,
  val username: String,
  val firstName: String?,
  val lastName: String?,
  val nickname: String?,
  val email: String?)