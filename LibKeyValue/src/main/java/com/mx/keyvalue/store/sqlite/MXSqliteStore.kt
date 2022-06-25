package com.mx.keyvalue.store.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mx.keyvalue.secret.IMXCrypt
import com.mx.keyvalue.store.IKVStore
import com.mx.keyvalue.utils.MXUtils
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class MXSqliteStore(
    private val context: Context,
    private val dbName: String,
    private val crypt: IMXCrypt
) : IKVStore {
    private val lock = ReentrantReadWriteLock(true)
    private var sqliteHelper: SQLiteOpenHelper? = null

    private fun getDatabase(): SQLiteDatabase {
        if (sqliteHelper == null) {
            synchronized(this) {
                if (sqliteHelper == null) {
                    sqliteHelper = MXSQLiteHelper(context, dbName)
                }
            }
        }
        return sqliteHelper!!.writableDatabase
    }

    override fun get(key: String): String? {
        lock.read {
            var cursor: Cursor? = null
            try {
                val database = getDatabase()
                cursor = database.query(
                    dbName,
                    arrayOf(
                        MXSQLiteHelper.DB_KEY_NAME,
                        MXSQLiteHelper.DB_KEY_VALUE,
                        MXSQLiteHelper.DB_KEY_SALT,
                        MXSQLiteHelper.DB_KEY_DEAD_TIME
                    ),
                    "${MXSQLiteHelper.DB_KEY_NAME}=?",
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
                val salt = crypt.generalSalt()
                val value_encrypt = crypt.encrypt(key, value, salt)

                val values = ContentValues()
                values.put(MXSQLiteHelper.DB_KEY_NAME, key)
                values.put(MXSQLiteHelper.DB_KEY_VALUE, value_encrypt)
                values.put(MXSQLiteHelper.DB_KEY_SALT, salt)
                values.put(MXSQLiteHelper.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
                values.put(MXSQLiteHelper.DB_KEY_DEAD_TIME, dead_time ?: 0L)
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
                    "${MXSQLiteHelper.DB_KEY_NAME}=?",
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
                        MXSQLiteHelper.DB_KEY_NAME,
                        MXSQLiteHelper.DB_KEY_VALUE,
                        MXSQLiteHelper.DB_KEY_SALT,
                        MXSQLiteHelper.DB_KEY_DEAD_TIME
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
                    "${MXSQLiteHelper.DB_KEY_DEAD_TIME}>0 and ${MXSQLiteHelper.DB_KEY_DEAD_TIME}<?",
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
                cursor.getColumnIndexOrThrow(MXSQLiteHelper.DB_KEY_NAME)
            )
            var value = cursor.getString(
                cursor.getColumnIndexOrThrow(MXSQLiteHelper.DB_KEY_VALUE)
            )
            val salt = cursor.getString(
                cursor.getColumnIndexOrThrow(MXSQLiteHelper.DB_KEY_SALT)
            )
            val dead_time = cursor.getLong(
                cursor.getColumnIndexOrThrow(MXSQLiteHelper.DB_KEY_DEAD_TIME)
            )
            if (dead_time > 0 && dead_time < System.currentTimeMillis()) {
                return null
            }

            value = crypt.decrypt(key, value, salt)
            return Pair(key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun release() {
        synchronized(this) {
            sqliteHelper?.close()
            sqliteHelper = null
        }
    }
}