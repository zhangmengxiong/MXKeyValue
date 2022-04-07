package com.mx.keyvalue

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mx.keyvalue.secret.IMXSecret

class DBKeyValue(
    private val context: Context,
    private val dbName: String,
    private val mxSecret: IMXSecret
) : IKeyValue {
    private val lock = Object()
    private val dbHelp = DBHelp(context)
    override fun get(key: String): String? {
        synchronized(lock) {
            val database = dbHelp.readableDatabase
            var value: String? = null
            var secret: String? = null
            var cursor: Cursor? = null
            try {
                cursor = database.query(
                    DBHelp.DB_NAME,
                    arrayOf(DBHelp.DB_KEY_VALUE, DBHelp.DB_KEY_SECRET),
                    "${DBHelp.DB_KEY_NAME}=? and ${DBHelp.DB_KEY_TABLE}=?",
                    arrayOf(key, dbName),
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    value = cursor.getString(cursor.getColumnIndexOrThrow(DBHelp.DB_KEY_VALUE))
                    secret = cursor.getString(cursor.getColumnIndexOrThrow(DBHelp.DB_KEY_SECRET))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    cursor?.close()
                } catch (e: Exception) {
                }
                try {
                    database.close()
                } catch (e: Exception) {
                }
            }
            if (secret != null && value != null) {
                value = mxSecret.decryptValue(key, value, secret)
            }

            return value
        }
    }

    override fun set(key: String, value: String?): Boolean {
        synchronized(lock) {
            try {
                val database = dbHelp.writableDatabase
                val secret = mxSecret.generalSecret()
                val value_encrypt = mxSecret.encryptValue(key, value, secret)

                val values = ContentValues()
                values.put(DBHelp.DB_KEY_NAME, key)
                values.put(DBHelp.DB_KEY_TABLE, dbName)
                values.put(DBHelp.DB_KEY_VALUE, value_encrypt)
                values.put(DBHelp.DB_KEY_SECRET, secret)
                values.put(DBHelp.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
                return database.replace(DBHelp.DB_NAME, null, values) >= 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    override fun cleanAll(): Boolean {
        synchronized(lock) {
            try {
                val database = dbHelp.writableDatabase
                return database.delete(
                    DBHelp.DB_NAME, "${DBHelp.DB_KEY_TABLE}=?",
                    arrayOf(dbName),
                ) > 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }
}

private class DBHelp(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    companion object {
        const val DB_NAME = "mx_kv_db_v1"
        const val DB_KEY_TABLE = "kv_table_name"
        const val DB_KEY_NAME = "kv_name"
        const val DB_KEY_VALUE = "kv_value"
        const val DB_KEY_SECRET = "kv_secret"
        const val DB_KEY_UPDATE_TIME = "update_time"

        private const val DB_CREATE =
            "create table $DB_NAME($DB_KEY_NAME varchar(512) ," +
                    "$DB_KEY_TABLE varchar(512) ," +
                    "$DB_KEY_VALUE text ," +
                    "$DB_KEY_SECRET varchar(64)," +
                    "$DB_KEY_UPDATE_TIME long," +
                    "primary key ($DB_KEY_NAME, $DB_KEY_TABLE))"

        private const val DB_DROP = "DROP TABLE IF EXISTS $DB_NAME"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DB_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DB_DROP)
        db?.execSQL(DB_CREATE)
    }
}