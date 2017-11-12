@file:Suppress("FunctionName")

package com.dimsuz.yamm.data.sources.db.models

data class UserDbModel(
  val id: String,
  val username: String,
  val firstName: String?,
  val lastName: String?,
  val nickname: String?,
  val email: String?
) : UserDbSqlDelightModel {

  companion object {
    val FACTORY = UserDbSqlDelightModel.Factory<UserDbModel>(::UserDbModel)
  }

  override fun id() = id
  override fun username() = username
  override fun first_name() = firstName
  override fun last_name() = lastName
  override fun nickname() = nickname
  override fun email() = email

}