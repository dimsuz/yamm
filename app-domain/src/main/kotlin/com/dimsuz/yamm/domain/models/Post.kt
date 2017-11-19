package com.dimsuz.yamm.domain.models

data class Post(
  val id: String,
  val userId: String,
  val channelId: String,
  val message: String,
  val type: Type
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