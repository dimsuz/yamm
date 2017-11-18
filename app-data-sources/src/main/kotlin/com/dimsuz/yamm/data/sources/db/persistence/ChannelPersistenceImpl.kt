package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.dimsuz.yamm.data.sources.db.models.ChannelDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.models.ChannelResolvedDbModel
import com.dimsuz.yamm.data.sources.db.models.UserDbModel
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