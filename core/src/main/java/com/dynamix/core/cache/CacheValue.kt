package com.dynamix.core.cache

data class CacheValue<T : Any>(
    val value: T?,
    val timeOut: Long = -1L,
    val insertTime: Long = System.currentTimeMillis(),
    val lastAccessed: Long = -1L,

    val groupKey: Any? = "",
    val key: Any = "",
    val shouldCache: Boolean = true,
    // This is used when API call is needed even if data is in cache
    val retrieveCache: Boolean = true,
    val cacheType: CacheType = CacheType.CACHE_TYPE_NONE
)

enum class CacheType {
    CACHE_TYPE_APPLICATION_DATA, CACHE_TYPE_INTERMEDIATE_DATA, CACHE_TYPE_SESSION_DATA, CACHE_TYPE_PERMANENT, CACHE_TYPE_PERMANENT_GROUP, CACHE_TYPE_NONE
}