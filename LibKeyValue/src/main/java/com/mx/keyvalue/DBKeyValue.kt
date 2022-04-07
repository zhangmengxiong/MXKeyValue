package com.mx.keyvalue

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mx.keyvalue.secret.IMXSecret

class DBKeyValue(context: Context, private val mxSecret: IMXSecret) : IKeyValue {
    private val dbHelp = DBHelp(context).writableDatabase
    override fun get(key: String): String? {
        var cursor: Cursor? = null
        var value: String? = null
        var secret: String? = null
        try {
            cursor = dbHelp.query(
                DBHelp.DB_NAME,
                arrayOf(DBHelp.DB_KEY_VALUE, DBHelp.DB_KEY_SECRET),
                "${DBHelp.DB_KEY_NAME}=?",
                arrayOf(key),
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
        }
        if (secret != null && value != null) {
            value = mxSecret.decryptValue(key, value, secret)
        }

        return value
    }

    override fun set(key: String, value: String): Boolean {
        try {
            val secret = mxSecret.generalSecret()
            val value_encrypt = mxSecret.encryptValue(key, value, secret)

            val values = ContentValues()
            values.put(DBHelp.DB_KEY_NAME, key)
            values.put(DBHelp.DB_KEY_VALUE, value_encrypt)
            values.put(DBHelp.DB_KEY_SECRET, secret)
            values.put(DBHelp.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
            return dbHelp.insert(DBHelp.DB_NAME, null, values) >= 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun cleanAll(): Boolean {
        return dbHelp.delete(DBHelp.DB_NAME, null, null) > 0
    }
}

private class DBHelp(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    companion object {
        const val DB_NAME = "mx_kv_db"
        const val DB_KEY_NAME = "kv_name"
        const val DB_KEY_VALUE = "kv_value"
        const val DB_KEY_SECRET = "kv_secret"
        const val DB_KEY_UPDATE_TIME = "update_time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $DB_NAME($DB_KEY_NAME varchar(100) primary key , $DB_KEY_VALUE text , $DB_KEY_SECRET varchar(32), $DB_KEY_UPDATE_TIME long)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}