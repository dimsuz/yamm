package com.dimsuz.yamm.domain.models

data class User(
  val id: String,
  val username: String,
  val firstName: String?,
  val lastName: String?,
  val nickname: String?,
  val email: String?,
  val imageUrl: String?
) {
  val fullName get() = "$firstName $lastName"
}