# MXKeyValue
## 介绍
基于Sqlite的，支持加密、自定义加密方式的KV数据库
[![](https://jitpack.io/v/com.gitee.zhangmengxiong/MXKeyValue.svg)](https://jitpack.io/#com.gitee.zhangmengxiong/MXKeyValue)
库引用： 替换1.0.9 为最新版本
```gradle
    implementation 'com.gitee.zhangmengxiong:MXKeyValue:1.0.9'
```

## 使用方法

```kotlin
val KV = MXKeyValue(
    application, // context
    name = "mx_kv_test", // 存储数据库名称
    secret = MXNoSecret() // 加密方式
)

// 清理所有KV
KV.cleanAll()

// 获取所有KV
KV.getAll()

// 清理失效的KeyValue
KV.cleanExpire()

// 设置KV
KV.set(key, value)

// 设置KV的有效期 1分钟 后失效
KV.set(key, value, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1))

// 获取Value
KV.get("test_expire_key")

// 获取Value ，如果不存在则返回默认值
KV.get("test_expire_key", default = "默认值")

// 删除key
KV.delete("test_expire_key")

// 从SharedPreferences拷贝所有数据
KV.cloneFromSharedPreferences("sp_name")
```

## 加密相关
MXKeyValue内置两种加密方式：
- MXNoSecret()
- MXAESSecret("加密字符")

### MXNoSecret
MXNoSecret = 不加密，存储Value=设置的Value

### MXAESSecret
MXAESSecret = AES对称加密
- 存储的value=encrypt(设置的Value)
- 读取的Value=decrypt(存储的Value)

注意：MXAESSecret初始化的加密字符在app上线后不能修改，否则会导致数据读取错误！

### 自定义加密方式
- 需要实现IMXSecret接口
- generalSalt() = 生成当条记录的混淆字段
- encrypt方法 = 源数据Value->存储Value
- decrypt方法 = 存储Value->源数据Value
```kotlin
class MySecret : IMXSecret {
    private val divider = "$$$$$$$$$$$$"
    override fun generalSalt(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    override fun encrypt(key: String, value: String, salt: String): String? {
        return "$key$divider$value"
    }

    override fun decrypt(key: String, secretValue: String, salt: String): String? {
        return secretValue.split(divider).lastOrNull()
    }
}
```
使用方法：
```kotlin
val KV = MXKeyValue(
    application,
    name = "mx_kv_test",
    secret = MySecret() // 加密方式
)
```