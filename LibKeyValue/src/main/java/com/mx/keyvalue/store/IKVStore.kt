package com.mx.keyvalue.store

import android.content.Context
import com.mx.keyvalue.crypt.IKVCrypt

interface IKVStore {
    /**
     * 初始化
     * @param context 上下文，一般需要传入applicationContext
     * @param name 存储库名字
     * @param crypt 加密方式
     */
    fun create(context: Context, name: String, crypt: IKVCrypt)

    /**
     * 获取Value
     */
    fun get(key: String): String?

    /**
     * 设置Value
     * @param key 键
     * @param value 值
     * @param dead_time 过期时间，超过这个时间后读取Key会返回 NULL
     */
    fun set(key: String, value: String, dead_time: Long?): Boolean

    /**
     * 删除键值对
     * @param key 键
     */
    fun delete(key: String): Boolean

    /**
     * 获取所有键值对
     */
    fun getAll(): Map<String, String>

    /**
     * 清理所有超时的键值对
     */
    fun cleanExpire(): Boolean

    /**
     * 删除所有数据
     */
    fun cleanAll(): Boolean

    /**
     * 释放资源
     */
    fun release()
}