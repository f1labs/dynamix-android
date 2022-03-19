package com.dynamix.core.cache.impl

import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.provider.SessionCacheProvider
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SessionCacheProviderImpl : CacheImpl(), SessionCacheProvider {

    private val updateNotifier: PublishSubject<Any> = PublishSubject.create()

    override fun <T : Any> insert(key: Any, value: CacheValue<T>) {
        super.insert(key, value)
        updateNotifier.onNext(key)
    }

    override fun <T : Any> insertOrUpdate(key: Any, data: CacheValue<T>) {
        val storedData = getOrDefault<T>(key)
        if (storedData.value != null) {
            insert(key, storedData.copy(data.value))
        } else {
            insert(key, data)
        }
    }

    override fun <T : Any> queryObservable(key: Any): Observable<CacheValue<T>> {
        return if (storage.containsKey(key)) {
            updateNotifier.filter { it == key }
                .map { getOrDefault<T>(key) }
                .startWith(Observable.just(getOrDefault(key)))
        } else {
            updateNotifier.filter { it == key }
                .map { getOrDefault(it) }
        }
    }
}