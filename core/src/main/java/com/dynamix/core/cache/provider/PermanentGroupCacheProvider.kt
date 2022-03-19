package com.dynamix.core.cache.provider

interface PermanentGroupCacheProvider : CacheProvider {

    fun removeGroup(groupKey: String)
}