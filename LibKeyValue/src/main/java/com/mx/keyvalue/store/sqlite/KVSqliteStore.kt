package com.mx.keyvalue.store.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mx.keyvalue.crypt.IKVCrypt
import com.mx.keyvalue.crypt.KVNoCrypt
import com.mx.keyvalue.store.IKVStore
import com.mx.keyvalue.utils.KVUtils
import com.mx.keyvalue.utils.KeyFilter
import com.mx.keyvalue.utils.MXPosition
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * sqlite实现的存储
 */
class KVSqliteStore : IKVStore {
    private val lock = ReentrantReadWriteLock(true)
    private var context: Context? = null
    private var name: String = ""
    private var crypt: IKVCrypt = KVNoCrypt()
    private var sqliteHelper: SQLiteOpenHelper? = null

    private fun getDatabase(): SQLiteDatabase {
        if (sqliteHelper == null) {
            synchronized(this) {
                if (sqliteHelper == null) {
                    sqliteHelper = KVSQLiteHelper(context!!, name)
                }
            }
        }
        return sqliteHelper!!.writableDatabase
    }

    override fun create(context: Context, name: String, crypt: IKVCrypt) {
        this.context = context
        this.name = name
        this.crypt = crypt

        this.sqliteHelper = KVSQLiteHelper(context, name)
    }

    override fun get(key: String): String? {
        lock.read {
            var cursor: Cursor? = null
            try {
                val database = getDatabase()
                cursor = database.query(
                    name,
                    arrayOf(
                        KVSQLiteHelper.DB_KEY_NAME,
                        KVSQLiteHelper.DB_KEY_VALUE,
                        KVSQLiteHelper.DB_KEY_SALT,
                        KVSQLiteHelper.DB_KEY_DEAD_TIME
                    ),
                    "${KVSQLiteHelper.DB_KEY_NAME}=?",
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
                KVUtils.log("get错误 -> $key -- ${e.message}")
            } finally {
                try {
                    cursor?.close()
                } catch (e: Exception) {
                }
            }
        }
        return null
    }

    override fun set(key: String, value: String, dead_time: Long): Boolean {
        lock.write {
            val database = getDatabase()
            try {
                database.beginTransaction()
                val salt = crypt.generalSalt()
                val value_encrypt = crypt.encrypt(key, value, salt)

                val values = ContentValues()
                values.put(KVSQLiteHelper.DB_KEY_NAME, key)
                values.put(KVSQLiteHelper.DB_KEY_VALUE, value_encrypt)
                values.put(KVSQLiteHelper.DB_KEY_SALT, salt)
                values.put(KVSQLiteHelper.DB_KEY_UPDATE_TIME, System.currentTimeMillis())
                values.put(KVSQLiteHelper.DB_KEY_DEAD_TIME, dead_time)
                val result = database.replace(name, null, values) >= 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                KVUtils.log("set错误 -> $key = $value ; $dead_time -- ${e.message}")
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
                    name,
                    "${KVSQLiteHelper.DB_KEY_NAME}=?",
                    arrayOf(key)
                ) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                KVUtils.log("delete错误 -> $key -- ${e.message}")
            } finally {
                database.endTransaction()
            }
        }
        return false
    }

    override fun deleteFilter(key: KeyFilter): Boolean {
        lock.write {
            val key_filter = when (key.position) {
                MXPosition.START -> "${key.filter}%"
                MXPosition.END -> "%${key.filter}"
                MXPosition.ANY -> "%${key.filter}%"
            }

            val database = getDatabase()
            try {
                database.beginTransaction()
                val result = database.delete(
                    name,
                    "${KVSQLiteHelper.DB_KEY_NAME} like ?",
                    arrayOf(key_filter)
                ) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                KVUtils.log("delete错误 -> $key -- ${e.message}")
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
                    name,
                    arrayOf(
                        KVSQLiteHelper.DB_KEY_NAME,
                        KVSQLiteHelper.DB_KEY_VALUE,
                        KVSQLiteHelper.DB_KEY_SALT,
                        KVSQLiteHelper.DB_KEY_DEAD_TIME
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
                KVUtils.log("getAll错误 -> ${e.message}")
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
                    name,
                    "${KVSQLiteHelper.DB_KEY_DEAD_TIME}>0 and ${KVSQLiteHelper.DB_KEY_DEAD_TIME}<?",
                    arrayOf(System.currentTimeMillis().toString())
                ) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                KVUtils.log("cleanExpire错误 -> ${e.message}")
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
                val result = database.delete(name, null, null) > 0
                database.setTransactionSuccessful()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                KVUtils.log("cleanAll错误 -> ${e.message}")
            } finally {
                database.endTransaction()
            }
        }
        return false
    }

    private fun cursorToEntry(cursor: Cursor): Pair<String, String>? {
        try {
            val key = cursor.getString(
                cursor.getColumnIndexOrThrow(KVSQLiteHelper.DB_KEY_NAME)
            )
            var value = cursor.getString(
                cursor.getColumnIndexOrThrow(KVSQLiteHelper.DB_KEY_VALUE)
            )
            val salt = cursor.getString(
                cursor.getColumnIndexOrThrow(KVSQLiteHelper.DB_KEY_SALT)
            )
            val dead_time = cursor.getLong(
                cursor.getColumnIndexOrThrow(KVSQLiteHelper.DB_KEY_DEAD_TIME)
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