package com.dimsuz.yamm.domain.models

data class Channel(
  val id: String,
  val displayName: String,
  val name: String,
  val header: String?,
  val purpose: String?
)
