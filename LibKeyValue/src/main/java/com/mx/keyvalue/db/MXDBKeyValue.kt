package com.mx.keyvalue.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.mx.keyvalue.IMXKeyValue
import com.mx.keyvalue.secret.IMXSecret

internal class MXDBKeyValue(
    context: Context,
    private val dbName: String,
    private val mxSecret: IMXSecret
) : IMXKeyValue {
    private val lock = Object()
    private val openHelper = MXKVSQLiteOpenHelper(context, dbName)

    override fun get(key: String): String? {
        synchronized(lock) {
            val database = openHelper.readableDatabase
            var value: String? = null
            var secret: String? = null
            var cursor: Cursor? = null
            try {
                cursor = database.query(
                    dbName,
                    arrayOf(MXKVSQLiteOpenHelper.DB_KEY_VALUE, MXKVSQLiteOpenHelper.DB_KEY_SECRET),
                    "${MXKVSQLiteOpenHelper.DB_KEY_NAME}=?", arrayOf(key), null, null, null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    value =
                        cursor.getString(cursor.getColumnIndexOrThrow(MXKVSQLiteOpenHelper.DB_KEY_VALUE))
                    secret =
                        cursor.getString(cursor.getColumnIndexOrThrow(MXKVSQLiteOpenHelper.DB_KEY_SECRET))
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
                val database = openHelper.writableDatabase
                val secret = mxSecret.generalSecret()
                val value_encrypt = mxSecret.encryptValue(key, value, secret)

                val values = ContentValues()
                values.put(MXKVSQLiteOpenHelper.DB_KEY_NAME, key)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_VALUE, value_encrypt)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_SECRET, secret)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
                return database.replace(dbName, null, values) >= 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    override fun cleanAll(): Boolean {
        synchronized(lock) {
            try {
                val database = openHelper.writableDatabase
                return database.delete(dbName, null, null) > 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }
}