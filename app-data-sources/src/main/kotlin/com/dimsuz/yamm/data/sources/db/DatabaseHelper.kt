package com.dimsuz.yamm.data.sources.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dimsuz.yamm.data.sources.db.models.ChannelDbSqlDelightModel
import com.dimsuz.yamm.data.sources.db.models.UserDbSqlDelightModel

private const val DATABASE_NAME = "yamm.db"
private const val DATABASE_VERSION = 1

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  override fun onCreate(db: SQLiteDatabase) {
    db.rawQuery(ChannelDbSqlDelightModel.CREATE_TABLE, null)
    db.rawQuery(UserDbSqlDelightModel.CREATE_TABLE, null)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
  }
}