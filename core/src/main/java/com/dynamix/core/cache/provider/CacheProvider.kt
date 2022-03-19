package com.dynamix.core.cache.provider

import com.dynamix.core.cache.CacheValue

interface CacheProvider {

    fun <T : Any> insert(key: Any, value: CacheValue<T>)

    fun <T : Any> query(key: Any): CacheValue<T>

    fun remove(key: Any)

    fun prune(threshold: Float = 0.5f)

    fun clear()
}