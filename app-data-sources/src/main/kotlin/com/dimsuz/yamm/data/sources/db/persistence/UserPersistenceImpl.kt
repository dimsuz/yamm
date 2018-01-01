package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.core.util.forEachApply
import com.dimsuz.yamm.data.sources.db.models.UserDbModel
import com.dimsuz.yamm.data.sources.db.models.UserDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.util.executeDelightStatement
import com.dimsuz.yamm.data.sources.db.util.inTransaction
import com.squareup.sqlbrite2.BriteDatabase
import javax.inject.Inject

internal class UserPersistenceImpl @Inject constructor(
  private val briteDatabase: BriteDatabase) : UserPersistence {

  override fun getUserById(id: String): UserDbModel? {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        UserDbModel.FACTORY.select_one_by_id(id),
        UserDbModel.FACTORY.select_one_by_idMapper())
      .firstOrNull()
  }

  override fun findNonExistingIds(ids: Set<String>): List<String> {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        UserDbModel.FACTORY.select_ids(ids.toTypedArray()),
        UserDbModel.FACTORY.select_idsMapper())
      .let { existingIds ->
        ids.minus(existingIds).toList()
      }
  }

  override fun getUsersById(ids: Set<String>): List<UserDbModel> {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        UserDbModel.FACTORY.select_by_id(ids.toTypedArray()),
        UserDbModel.FACTORY.select_by_idMapper())
  }

  override fun replaceUsers(users: List<UserDbModel>) {
    val insertOrReplace = UserDbSqlDelightModel.Insert_or_replace(briteDatabase.writableDatabase)
    briteDatabase.inTransaction {
      users.forEachApply {
        insertOrReplace.bind(id, username, firstName, lastName, nickname, email, imageUrl)
        briteDatabase.executeInsert(insertOrReplace.table, insertOrReplace.program)
      }
    }
  }
}