package com.dimsuz.yamm.data.sources.db.util

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.squareup.sqlbrite2.BriteDatabase
import com.squareup.sqldelight.RowMapper
import com.squareup.sqldelight.SqlDelightCompiledStatement
import com.squareup.sqldelight.SqlDelightStatement

@SuppressLint("Recycle")
internal fun <T> SQLiteDatabase.executeDelightStatement(statement: SqlDelightStatement, mapper: RowMapper<T>): List<T> {
  val cursor = this.rawQuery(statement.statement, statement.args)
  return cursor.readAll(mapper)
}

internal inline fun BriteDatabase.inTransaction(code: BriteDatabase.() -> Unit) {
  val transaction = newTransaction()
  try {
    code()
    transaction.markSuccessful()
  } finally {
    transaction.end()
  }
}

internal fun BriteDatabase.executeInsert(statement: SqlDelightCompiledStatement): Long {
  return this.executeInsert(statement.table, statement.program)
}

internal fun BriteDatabase.executeUpdateDelete(statement: SqlDelightCompiledStatement): Int {
  return this.executeUpdateDelete(statement.table, statement.program)
}

internal fun <T> Cursor.readAll(mapper: RowMapper<T>): List<T> {
  return this.use {
    val items: MutableList<T> = mutableListOf()
    while (it.moveToNext()) {
      items.add(mapper.map(it))
    }
    items
  }
}

