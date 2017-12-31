@file:Suppress("FunctionName")

package com.dimsuz.yamm.data.sources.db.models

data class PostDbModel(
  val id: String,
  val userId: String,
  val channelId: String,
  val message: String?,
  val type: String,
  val createAt: Long,
  val updateAt: Long,
  val deleteAt: Long
) : PostDbSqlDelightModel {

  companion object {
    val FACTORY = PostDbSqlDelightModel.Factory<PostDbModel>(::PostDbModel)
  }

  override fun id() = id
  override fun user_id() = userId
  override fun channel_id() = channelId
  override fun message() = message
  override fun type() = type
  override fun create_at() = createAt
  override fun update_at() = updateAt
  override fun delete_at() = deleteAt

}

data class PostWithUserDbModel(
  val post: PostDbModel,
  val user: UserDbModel?
) : PostDbSqlDelightModel.Select_with_offsetModel<PostDbModel, UserDbModel> {

  override fun post() = post
  override fun user() = user
}