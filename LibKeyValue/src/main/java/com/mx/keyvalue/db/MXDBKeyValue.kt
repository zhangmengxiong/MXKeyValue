package com.mx.keyvalue.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.mx.keyvalue.base.IMXKeyValue
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
            var cursor: Cursor? = null
            try {
                cursor = database.query(
                    dbName,
                    arrayOf(
                        MXKVSQLiteOpenHelper.DB_KEY_NAME,
                        MXKVSQLiteOpenHelper.DB_KEY_VALUE,
                        MXKVSQLiteOpenHelper.DB_KEY_SALT,
                        MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME
                    ),
                    "${MXKVSQLiteOpenHelper.DB_KEY_NAME}=?",
                    arrayOf(key),
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val pair = cursorToEntry(cursor)
                    if (pair != null) {
                        return pair.second
                    }
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
        }
        return null
    }

    override fun set(key: String, value: String?, dead_time: Long?): Boolean {
        synchronized(lock) {
            val database = openHelper.writableDatabase
            try {
                val salt = mxSecret.generalSalt()
                val value_encrypt = mxSecret.encrypt(key, value, salt)

                val values = ContentValues()
                values.put(MXKVSQLiteOpenHelper.DB_KEY_NAME, key)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_VALUE, value_encrypt)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_SALT, salt)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
                values.put(MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME, dead_time ?: 0L)
                return database.replace(dbName, null, values) >= 0
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                database.close()
            }
        }
        return false
    }

    override fun getAll(): Map<String, String> {
        synchronized(lock) {
            val database = openHelper.readableDatabase
            val result = HashMap<String, String>()
            var cursor: Cursor? = null
            try {
                cursor = database.query(
                    dbName,
                    arrayOf(
                        MXKVSQLiteOpenHelper.DB_KEY_NAME,
                        MXKVSQLiteOpenHelper.DB_KEY_VALUE,
                        MXKVSQLiteOpenHelper.DB_KEY_SALT,
                        MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME
                    ), null, null, null, null, null
                )
                while (cursor.moveToNext()) {
                    val pair = cursorToEntry(cursor)
                    if (pair != null) {
                        result[pair.first] = pair.second
                    }
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
            return result
        }
    }

    override fun cleanExpire(): Boolean {
        synchronized(lock) {
            val database = openHelper.writableDatabase
            try {
                return database.delete(
                    dbName,
                    "${MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME}>0 and ${MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME}<?",
                    arrayOf(System.currentTimeMillis().toString())
                ) > 0
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    database.close()
                } catch (e: Exception) {
                }
            }
        }
        return false
    }

    override fun cleanAll(): Boolean {
        synchronized(lock) {
            val database = openHelper.writableDatabase
            try {
                return database.delete(dbName, null, null) > 0
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    database.close()
                } catch (e: Exception) {
                }
            }
        }
        return false
    }

    private fun cursorToEntry(cursor: Cursor): Pair<String, String>? {
        try {
            val key = cursor.getString(
                cursor.getColumnIndexOrThrow(MXKVSQLiteOpenHelper.DB_KEY_NAME)
            )
            var value = cursor.getString(
                cursor.getColumnIndexOrThrow(MXKVSQLiteOpenHelper.DB_KEY_VALUE)
            )
            val salt = cursor.getString(
                cursor.getColumnIndexOrThrow(MXKVSQLiteOpenHelper.DB_KEY_SALT)
            )
            val dead_time = cursor.getLong(
                cursor.getColumnIndexOrThrow(MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME)
            )
            if (dead_time > 0 && dead_time < System.currentTimeMillis()) {
                return null
            }

            value = mxSecret.decrypt(key, value, salt)
            return Pair(key, value)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }
}