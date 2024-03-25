package com.mx.keyvalue.utils

enum class MXPosition {
    /**
     * 模糊匹配 以Key开始
     */
    START,

    /**
     * 模糊匹配 以Key结束
     */
    END,

    /**
     * 模糊匹配 Key在字段中的任意位置
     */
    ANY
}

data class KeyFilter(
    val filter: String, val position: MXPosition = MXPosition.ANY
)