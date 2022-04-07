package com.mx.keyvalue.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class MXKVSQLiteOpenHelper(context: Context, dbName: String) :
    SQLiteOpenHelper(context, dbName, null, 3) {
    companion object {
        const val DB_KEY_NAME = "kv_name"
        const val DB_KEY_VALUE = "kv_value"
        const val DB_KEY_SALT = "kv_salt"
        const val DB_KEY_UPDATE_TIME = "update_time"
    }

    private val dbCreate = "create table $dbName($DB_KEY_NAME varchar(128) ," +
            "$DB_KEY_VALUE text ," +
            "$DB_KEY_SALT varchar(64) ," +
            "$DB_KEY_UPDATE_TIME long ," +
            "primary key ($DB_KEY_NAME))"
    private val dbDrop = "DROP TABLE IF EXISTS $dbName"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(dbCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(dbDrop)
        db?.execSQL(dbCreate)
    }
}