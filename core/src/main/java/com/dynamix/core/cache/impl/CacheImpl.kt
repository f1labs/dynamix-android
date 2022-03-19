package com.dynamix.core.cache.impl

import com.dynamix.core.cache.CacheValue
import java.util.concurrent.ConcurrentHashMap

abstract class CacheImpl {

    protected val storage: ConcurrentHashMap<Any, CacheValue<out Any>> = ConcurrentHashMap()

    open fun <T : Any> insert(key: Any, value: CacheValue<T>) {
        storage[key] = value
    }

    open fun <T : Any> query(key: Any): CacheValue<T> {
        return getOrDefault(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrDefault(key: Any): CacheValue<T> {
        val value = storage[key] as? CacheValue<T>
        return if (value != null && !isCacheValueExpired(value)) {
            // this value is not synchronized. Add synchronization would degrade the performance quite a bit
            // as this is read operation, so it is better to have inconsistent cache than slow cache
            value.copy(
                lastAccessed = System.currentTimeMillis()
            )
        } else {
            // remove the cache entry
            remove(key)
            CacheValue(null)
        }
    }

    private fun <T : Any> isCacheValueExpired(value: CacheValue<T>): Boolean {
        return if (value.timeOut == -1L) {
            false
        } else {
            (value.insertTime + value.timeOut) < System.currentTimeMillis()
        }
    }

    open fun remove(key: Any) {
        storage.remove(key)
    }

    open fun prune(threshold: Float = 0.5f) {
        // simple LRU implementation
        var removeCount = (threshold * getSize()).toInt()
        val sortedCache = storage.toList()
            .sortedBy {
                it.second.lastAccessed
            }

        // remove the keys from cache based on the sorted list
        for (i in sortedCache.indices) {
            if (removeCount < 1) {
                // check for expired keys
                if (isCacheValueExpired(sortedCache[i].second)) {
                    // remove this key as well
                    remove(sortedCache[i].first)
                }
            } else {
                remove(sortedCache[i].first)
                removeCount--
            }
        }
    }

    private fun getSize(): Int {
        return storage.size
    }

    open fun clear() {
        storage.clear()
    }
}