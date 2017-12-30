package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.core.util.forEachApply
import com.dimsuz.yamm.data.sources.db.models.PostDbModel
import com.dimsuz.yamm.data.sources.db.models.PostDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.util.inTransaction
import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.Observable
import javax.inject.Inject

internal class PostPersistenceImpl @Inject constructor(
  private val briteDatabase: BriteDatabase) : PostPersistence {

  override fun replacePosts(posts: List<PostDbModel>) {
    val insertOrReplace = PostDbSqlDelightModel.Insert_or_replace(briteDatabase.writableDatabase)
    briteDatabase.inTransaction {
      posts.forEachApply {
        insertOrReplace.bind(id, userId, channelId, message, type, createAt, updateAt, deleteAt)
        briteDatabase.executeInsert(insertOrReplace.table, insertOrReplace.program)
      }
    }
  }

  override fun getPostsLive(channelId: String, offset: Int, count: Int): Observable<List<PostDbModel>> {
    val query = PostDbModel.FACTORY.select_with_offset(channelId, count.toLong(), offset.toLong())
    val mapper = PostDbModel.FACTORY.select_with_offsetMapper()
    return briteDatabase.createQuery(query.tables, query.statement, *query.args)
      .mapToList(mapper::map)
  }
}