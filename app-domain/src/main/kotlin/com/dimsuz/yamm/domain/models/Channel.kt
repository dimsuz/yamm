package com.dimsuz.yamm.domain.models

data class Channel(
  val id: String,
  val type: Type,
  val displayName: String?,
  val name: String?,
  val header: String?,
  val purpose: String?
) {
  enum class Type {
    Open,
    Private,
    Direct,
    Group,
    Unknown
  }
}
