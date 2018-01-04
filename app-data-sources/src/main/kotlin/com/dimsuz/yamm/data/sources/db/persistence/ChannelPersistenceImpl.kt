package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.core.util.forEachApply
import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.dimsuz.yamm.data.sources.db.models.ChannelDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.models.ChannelResolvedDbModel
import com.dimsuz.yamm.data.sources.db.models.UserDbModel
import com.dimsuz.yamm.data.sources.db.util.executeDelightStatement
import com.dimsuz.yamm.data.sources.db.util.executeInsert
import com.dimsuz.yamm.data.sources.db.util.inTransaction
import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.Observable
import javax.inject.Inject

internal class ChannelPersistenceImpl @Inject constructor(
  private val briteDatabase: BriteDatabase): ChannelPersistence {

  override fun replaceUserChannels(channels: List<ChannelDbModel>) {
    briteDatabase.inTransaction {
      val insertChannel = ChannelDbSqlDelightModel.Insert_channel(writableDatabase)
      channels.forEachApply {
        insertChannel.bind(id, userId, teamId, type, displayName,
          name, header, purpose, teamMateId)
        briteDatabase.executeInsert(insertChannel)
      }
    }
  }

  override fun getUserChannels(userId: String, teamId: String): List<ChannelDbModel> {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        ChannelDbModel.FACTORY.user_channels(userId, teamId),
        ChannelDbModel.FACTORY.user_channelsMapper())
  }

  override fun getChannelByName(name: String, teamId: String): String? {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        ChannelDbModel.FACTORY.select_id_by_name(name, teamId),
        ChannelDbModel.FACTORY.select_id_by_nameMapper())
      .firstOrNull()
  }

  override fun getUserChannelIds(userId: String, teamId: String): List<String> {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        ChannelDbModel.FACTORY.select_all_ids(userId, teamId),
        ChannelDbModel.FACTORY.select_all_idsMapper())
  }

  override fun getUserChannelsLive(userId: String,
                                   teamId: String): Observable<List<ChannelResolvedDbModel>> {

    val query = ChannelDbModel.FACTORY.user_channels_resolved_teammates(userId, teamId)
    val mapper = ChannelDbModel.FACTORY.user_channels_resolved_teammatesMapper(
      ::ChannelResolvedDbModel, UserDbModel.FACTORY)

    return briteDatabase
      .createQuery(query.tables, query.statement, *query.args)
      .mapToList(mapper::map)
  }

}