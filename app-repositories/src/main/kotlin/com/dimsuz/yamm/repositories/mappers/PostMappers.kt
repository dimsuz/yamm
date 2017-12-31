package com.dimsuz.yamm.repositories.mappers

import com.dimsuz.yamm.data.sources.db.models.PostDbModel
import com.dimsuz.yamm.data.sources.db.models.PostWithUserDbModel
import com.dimsuz.yamm.data.sources.network.models.PostJson
import com.dimsuz.yamm.domain.errors.ModelMapException
import com.dimsuz.yamm.domain.errors.throwExpectedNotNull
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.domain.models.User
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

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

fun PostJson.toDomainModel(user: User): Post {
  return Post(
    id = this.id ?: throwExpectedNotNull("post", "id"),
    user = user,
    channelId = this.channel_id ?: throwExpectedNotNull("post", "channel_id"),
    message = this.message.orEmpty(),
    type = this.type?.toPostType() ?: throwExpectedNotNull("post", "type"),
    createAt = this.create_at?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) } ?: LocalDateTime.MIN,
    updateAt = this.update_at?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) } ?: LocalDateTime.MIN,
    deleteAt = this.delete_at?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) } ?: LocalDateTime.MIN
  )
}

fun Post.toDatabaseModel(): PostDbModel {
  return PostDbModel(
    id = this.id,
    userId = this.user.id,
    channelId = this.channelId,
    message = this.message,
    type = this.type.toDbPostType(),
    createAt = this.createAt.toEpochSecond(ZoneOffset.UTC),
    updateAt = this.updateAt.toEpochSecond(ZoneOffset.UTC),
    deleteAt = this.deleteAt.toEpochSecond(ZoneOffset.UTC)
  )
}

fun PostDbModel.toDomainModel(user: User): Post {
  return Post(
    id = this.id,
    user = user,
    channelId = this.channelId,
    message = this.message.orEmpty(),
    type = this.type.toPostType(),
    createAt = LocalDateTime.ofEpochSecond(this.createAt, 0, ZoneOffset.UTC),
    updateAt = LocalDateTime.ofEpochSecond(this.updateAt, 0, ZoneOffset.UTC),
    deleteAt = LocalDateTime.ofEpochSecond(this.deleteAt, 0, ZoneOffset.UTC)
    )
}

fun PostWithUserDbModel.toDomainModel(): Post {
  // users are supposed to be fetched along with the posts (if absent)
  val user = this.user ?: throw ModelMapException("failed to find user for post in DB, userId=${this.post.userId}")
  return this.post.toDomainModel(user.toDomainModel())
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

private fun Post.Type.toDbPostType(): String {
  return when (this) {
    Post.Type.Default -> ""
    Post.Type.SlackAttachment -> "slack_attachment"
    Post.Type.SystemGeneric -> "system_generic"
    Post.Type.JoinChannel -> "system_join_channel"
    Post.Type.LeaveChannel -> "system_leave_channel"
    Post.Type.AddToChannel -> "system_add_to_channel"
    Post.Type.RemoveFromChannel -> "system_remove_from_channel"
    Post.Type.HeaderChange -> "system_header_change"
    Post.Type.DisplayNameChange -> "system_displayname_change"
    Post.Type.PurposeChange -> "system_purpose_change"
    Post.Type.ChannelDeleted -> "system_channel_deleted"
    Post.Type.Ephemeral -> "system_ephemeral"
  }
}
