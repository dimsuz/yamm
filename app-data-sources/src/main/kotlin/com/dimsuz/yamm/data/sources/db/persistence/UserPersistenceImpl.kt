package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.UserDbModel
import com.dimsuz.yamm.data.sources.db.models.UserDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.util.executeDelightStatement
import com.squareup.sqlbrite2.BriteDatabase

internal class UserPersistenceImpl(private val briteDatabase: BriteDatabase) : UserPersistence {

  override fun getUsersById(ids: List<String>): List<UserDbModel> {
    return briteDatabase.readableDatabase
      .executeDelightStatement(
        UserDbModel.FACTORY.select_by_id(ids.toTypedArray()),
        UserDbModel.FACTORY.select_by_idMapper())
  }

  override fun replaceUsers(users: List<UserDbModel>) {
    val insertOrReplace = UserDbSqlDelightModel.Insert_or_replace(briteDatabase.writableDatabase)
    for (u in users) {
      with (u) {
        insertOrReplace.bind(id, username, firstName, lastName, nickname, email)
        briteDatabase.executeInsert(insertOrReplace.table, insertOrReplace.program)
      }
    }
  }
}