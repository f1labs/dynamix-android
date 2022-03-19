package com.dynamix.core.cache.manager

import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.provider.CacheProvider

interface CacheManagerProvider {

    fun getCacheManager(cacheType: String): CacheManagerProvider

    fun getProvider(cacheType: String): CacheProvider

    fun <T : Any> insertData(key: Any, value: CacheValue<T>)

    fun <T : Any> queryData(key: Any): CacheValue<T>

    fun removeData(key: Any)

    fun removeGroup(groupKey: String)

    fun clearAll()
}