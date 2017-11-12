package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.ChannelDbModel
import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.Observable

internal class ChannelPersistenceImpl(private val briteDatabase: BriteDatabase): ChannelPersistence {

  override fun replaceUserChannels(channels: List<ChannelDbModel>) {
    TODO("not implemented")
  }

  override fun getUserChannels(userId: String, teamId: String): List<ChannelDbModel> {
    TODO("not implemented")
  }

  override fun getUserChannelsLive(userId: String, teamId: String): Observable<List<ChannelDbModel>> {
    TODO("not implemented")
  }
}