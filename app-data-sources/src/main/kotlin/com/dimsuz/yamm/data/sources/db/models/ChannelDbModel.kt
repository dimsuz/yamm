@file:Suppress("FunctionName")

package com.dimsuz.yamm.data.sources.db.models

data class ChannelDbModel(
  val id: String,
  val userId: String,
  val teamId: String,
  val type: String,
  val displayName: String?,
  val name: String?,
  val header: String?,
  val purpose: String?,
  val teamMateId: String?

) : ChannelDbSqlDelightModel {

  companion object {
    val FACTORY = ChannelDbSqlDelightModel.Factory<ChannelDbModel>(::ChannelDbModel)
  }

  override fun id() = id
  override fun user_id() = userId
  override fun team_id() = teamId
  override fun type() = type
  override fun display_name() = displayName
  override fun name() = name
  override fun header() = header
  override fun purpose() = purpose
  override fun team_mate_id() = teamMateId

}