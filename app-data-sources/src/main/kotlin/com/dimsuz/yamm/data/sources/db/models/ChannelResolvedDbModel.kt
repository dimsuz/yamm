package com.dimsuz.yamm.data.sources.db.models

data class ChannelResolvedDbModel(
  val channel: ChannelDbModel,
  /**
   * A team mate model is attached only for direct channels
   */
  val teamMate: UserDbModel?
) : ChannelDbSqlDelightModel.User_channels_resolved_teammatesModel<ChannelDbModel, UserDbModel>,
  ChannelDbSqlDelightModel.Select_by_idModel<ChannelDbModel, UserDbModel>
{

  override fun channel() = channel
  override fun user() = teamMate
}