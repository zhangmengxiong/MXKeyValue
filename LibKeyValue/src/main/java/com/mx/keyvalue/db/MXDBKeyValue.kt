package com.mx.keyvalue.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mx.keyvalue.base.IMXKeyValue
import com.mx.keyvalue.secret.IMXSecret
import com.mx.keyvalue.utils.MXUtils
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class MXDBKeyValue(
    private val context: Context,
    private val dbName: String,
    private val mxSecret: IMXSecret
) : IMXKeyValue {
    private val lock = ReentrantReadWriteLock(true)
    private var openHelper: SQLiteOpenHelper? = null

    private fun getDatabase(): SQLiteDatabase {
        var sqLiteOpenHelper = openHelper
        if (sqLiteOpenHelper == null) {
            synchronized(this) {
                if (sqLiteOpenHelper == null) {
                    sqLiteOpenHelper = MXKVSQLiteOpenHelper(context, dbName)
                    this.openHelper = sqLiteOpenHelper
                }
            }
        }
        return sqLiteOpenHelper!!.writableDatabase
    }

    override fun get(key: String): String? {
        lock.read {
            var cursor: Cursor? = null
            try {
                val database = getDatabase()
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
                MXUtils.log("get错误 -> $key -- ${e.message}")
            } finally {
                try {
                    cursor?.close()
                } catch (e: Exception) {
                }
            }
        }
        return null
    }

    override fun set(key: String, value: String, dead_time: Long?): Boolean {
        lock.write {
            val database = getDatabase()
            try {
                database.beginTransaction()
                val salt = mxSecret.generalSalt()
                val value_encrypt = mxSecret.encrypt(key, value, salt)

                val values = ContentValues()
                values.put(MXKVSQLiteOpenHelper.DB_KEY_NAME, key)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_VALUE, value_encrypt)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_SALT, salt)
                values.put(MXKVSQLiteOpenHelper.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
                values.put(MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME, dead_time ?: 0L)
                val result = database.replace(dbName, null, values) >= 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                MXUtils.log("set错误 -> $key = $value ; $dead_time -- ${e.message}")
            } finally {
                database.endTransaction()
            }
        }
        return false
    }

    override fun delete(key: String): Boolean {
        lock.write {
            val database = getDatabase()
            try {
                database.beginTransaction()
                val result = database.delete(
                    dbName,
                    "${MXKVSQLiteOpenHelper.DB_KEY_NAME}=?",
                    arrayOf(key)
                ) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                MXUtils.log("delete错误 -> $key -- ${e.message}")
            } finally {
                database.endTransaction()
            }
        }
        return false
    }

    override fun getAll(): Map<String, String> {
        lock.read {
            val database = getDatabase()
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
                MXUtils.log("getAll错误 -> ${e.message}")
            } finally {
                try {
                    cursor?.close()
                } catch (e: Exception) {
                }
            }
            return result
        }
    }

    override fun cleanExpire(): Boolean {
        lock.write {
            val database = getDatabase()
            try {
                database.beginTransaction()
                val result = database.delete(
                    dbName,
                    "${MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME}>0 and ${MXKVSQLiteOpenHelper.DB_KEY_DEAD_TIME}<?",
                    arrayOf(System.currentTimeMillis().toString())
                ) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                MXUtils.log("cleanExpire错误 -> ${e.message}")
            } finally {
                database.endTransaction()
            }
        }
        return false
    }

    override fun cleanAll(): Boolean {
        lock.write {
            val database = getDatabase()
            try {
                database.beginTransaction()
                val result = database.delete(dbName, null, null) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                MXUtils.log("cleanAll错误 -> ${e.message}")
            } finally {
                database.endTransaction()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun release() {
        openHelper?.close()
        openHelper = null
    }
}