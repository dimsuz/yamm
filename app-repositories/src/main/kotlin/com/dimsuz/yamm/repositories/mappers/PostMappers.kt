package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.db.models.PostDbModel
import com.dimsuz.yamm.data.sources.network.models.PostJson
import com.dimsuz.yamm.domain.errors.throwExpectedNotNull
import com.dimsuz.yamm.domain.models.Post

fun PostJson.toDatabaseModel(): PostDbModel {
  return PostDbModel(
    id = this.id ?: throwExpectedNotNull("post", "id"),
    userId = this.user_id ?: throwExpectedNotNull("post", "user_id"),
    channelId = this.channel_id ?: throwExpectedNotNull("post", "channel_id"),
    message = this.message,
    type = this.type ?: throwExpectedNotNull("post", "type"),
    createAt = this.create_at ?: 0,
    updateAt = this.update_at ?: 0,
    deleteAt = this.delete_at ?: 0
  )
}

fun PostJson.toDomainModel(): Post {
  return Post(
    id = this.id ?: throwExpectedNotNull("post", "id"),
    userId = this.user_id ?: throwExpectedNotNull("post", "user_id"),
    channelId = this.channel_id ?: throwExpectedNotNull("post", "channel_id"),
    message = this.message.orEmpty(),
    type = this.type?.toPostType() ?: throwExpectedNotNull("post", "type")
  )
}

fun PostDbModel.toDomainModel(): Post {
  return Post(
    id = this.id,
    userId = this.userId,
    channelId = this.channelId,
    message = this.message.orEmpty(),
    type = this.type.toPostType())
}

private fun String.toPostType(): Post.Type {
  return when (this) {
    "" -> Post.Type.Default
    "slack_attachment" -> Post.Type.SlackAttachment
    "system_generic" -> Post.Type.SystemGeneric
    "system_join_channel" -> Post.Type.JoinChannel
    "system_leave_channel" -> Post.Type.LeaveChannel
    "system_add_to_channel" -> Post.Type.AddToChannel
    "system_remove_from_channel" -> Post.Type.RemoveFromChannel
    "system_header_change" -> Post.Type.HeaderChange
    "system_displayname_change" -> Post.Type.DisplayNameChange
    "system_purpose_change" -> Post.Type.PurposeChange
    "system_channel_deleted" -> Post.Type.ChannelDeleted
    "system_ephemeral" -> Post.Type.Ephemeral
    else -> Post.Type.Default
  }
}
