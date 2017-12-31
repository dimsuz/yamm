package com.dimsuz.yamm.domain.models

import org.threeten.bp.LocalDateTime

data class Post(
  val id: String,
  val user: User,
  val channelId: String,
  val message: String,
  val type: Type,
  val createAt: LocalDateTime,
  val updateAt: LocalDateTime,
  val deleteAt: LocalDateTime
) {
  enum class Type {
    Default,
    SlackAttachment,
    SystemGeneric,
    JoinChannel,
    LeaveChannel,
    AddToChannel,
    RemoveFromChannel,
    HeaderChange,
    DisplayNameChange,
    PurposeChange,
    ChannelDeleted,
    Ephemeral
  }
}