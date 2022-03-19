package com.dynamix.core.cache.provider

import com.dynamix.core.cache.CacheValue
import io.reactivex.Observable

interface SessionCacheProvider : CacheProvider {

    fun <T : Any> insertOrUpdate(key: Any, data: CacheValue<T>)

    fun <T : Any> queryObservable(key: Any): Observable<CacheValue<T>>
}