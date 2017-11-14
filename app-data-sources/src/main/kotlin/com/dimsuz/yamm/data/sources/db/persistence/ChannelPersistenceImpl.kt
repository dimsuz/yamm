package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.dimsuz.yamm.data.sources.db.models.ChannelDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.util.executeDelightStatement
import com.dimsuz.yamm.data.sources.db.util.executeInsert
import com.dimsuz.yamm.data.sources.db.util.inTransaction
import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.Observable

internal class ChannelPersistenceImpl(private val briteDatabase: BriteDatabase): ChannelPersistence {

  override fun replaceUserChannels(channels: List<ChannelDbModel>) {
    briteDatabase.inTransaction {
      val insertChannel = ChannelDbSqlDelightModel.Insert_channel(writableDatabase)
      for (ch in channels) {
        with(ch) {
          insertChannel.bind(id, userId, teamId, type, displayName,
            name, header, purpose, teamMateId)
          briteDatabase.executeInsert(insertChannel)
        }
      }
    }
  }

  override fun getUserChannels(userId: String, teamId: String): List<ChannelDbModel> {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        ChannelDbModel.FACTORY.user_channels(userId, teamId),
        ChannelDbModel.FACTORY.user_channelsMapper())
  }

  override fun getUserChannelsLive(userId: String, teamId: String): Observable<List<ChannelDbModel>> {
    val query = ChannelDbModel.FACTORY.user_channels(userId, teamId)
    val mapper = ChannelDbModel.FACTORY.user_channelsMapper()
    return briteDatabase
      .createQuery(ChannelDbSqlDelightModel.TABLE_NAME, query.statement, *query.args)
      .mapToList(mapper::map)
  }
}