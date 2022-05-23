package com.dynamix.core.network

import com.dynamix.core.DataNotFoundException
import com.dynamix.core.cache.CacheType
import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.manager.CacheManagerProvider
import com.google.gson.Gson
import io.reactivex.Observable

class ApiProviderImpl(
    private val apiEndpoint: ApiEndpoint,
    private val apiRouteProvider: ApiRouteProvider,
    private val gson: Gson,
    private val cacheManagerProvider: CacheManagerProvider
) : ApiProvider {

    override fun <T : Any> get(routeCode: String, clazz: Class<T>): Observable<T> {
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.get(route.url)
                    .map {
                        if (it.isSuccessful && it.body() != null) {
                            return@map gson.fromJson(it.body(), clazz)
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> get(
        routeCode: String,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T> {
        val cachedData = filterDataInCache(cacheValue).value
        if (cachedData != null) {
            return Observable.just(cachedData)
        }
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.get(route.url)
                    .map {
                        if (it.isSuccessful && it.body() != null && it.body()!!.size() > 0) {
                            val response = gson.fromJson(it.body(), clazz)
                            cacheData(cacheValue.copy(response))
                            return@map response
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> getUrl(url: String, clazz: Class<T>): Observable<T> {
        return apiEndpoint.get(url)
            .map {
                if (it.isSuccessful && it.body() != null) {
                    return@map gson.fromJson(it.body(), clazz)
                }
                throw DataNotFoundException()
            }
    }

    override fun <T : Any> getUrl(
        url: String,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T> {
        val cachedData = filterDataInCache(cacheValue).value
        if (cachedData != null) {
            return Observable.just(cachedData)
        }
        return apiEndpoint.get(url)
            .map {
                if (it.isSuccessful && it.body() != null && it.body()!!.size() > 0) {
                    val response = gson.fromJson(it.body(), clazz)
                    cacheData(cacheValue.copy(response))
                    return@map response
                }
                throw DataNotFoundException()
            }
    }

    override fun <T : Any> get(routeCode: String, path: String, clazz: Class<T>): Observable<T> {
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.get(route.url + "/" + path)
                    .map {
                        if (it.isSuccessful && it.body() != null) {
                            return@map gson.fromJson(it.body(), clazz)
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> get(
        routeCode: String,
        path: String,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T> {
        val cachedData = filterDataInCache(cacheValue).value
        if (cachedData != null) {
            return Observable.just(cachedData)
        }
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.get(route.url + "/" + path)
                    .map {
                        if (it.isSuccessful && it.body() != null && it.body()!!.size() > 0) {
                            val response = gson.fromJson(it.body(), clazz)
                            cacheData(cacheValue.copy(response))
                            return@map response
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> get(
        routeCode: String,
        params: Map<String, Any>,
        clazz: Class<T>
    ): Observable<T> {
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.get(route.url, params)
                    .map {
                        if (it.isSuccessful && it.body() != null) {
                            return@map gson.fromJson(it.body(), clazz)
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> get(
        routeCode: String,
        params: Map<String, Any>,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T> {
        val cachedData = filterDataInCache(cacheValue).value
        if (cachedData != null) {
            return Observable.just(cachedData)
        }
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.get(route.url, params)
                    .map {
                        if (it.isSuccessful && it.body() != null && it.body()!!.size() > 0) {
                            val response = gson.fromJson(it.body(), clazz)
                            cacheData(cacheValue.copy(response))
                            return@map response
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> post(routeCode: String, clazz: Class<T>): Observable<T> {
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.post(route.url)
                    .map {
                        if (it.isSuccessful && it.body() != null) {
                            return@map gson.fromJson(it.body(), clazz)
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> postUrl(url: String, clazz: Class<T>): Observable<T> {
        return apiEndpoint.post(url)
            .map {
                if (it.isSuccessful && it.body() != null) {
                    return@map gson.fromJson(it.body(), clazz)
                }
                throw DataNotFoundException()
            }
    }

    override fun <T : Any> post(
        routeCode: String,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T> {
        val cachedData = filterDataInCache(cacheValue).value
        if (cachedData != null) {
            return Observable.just(cachedData)
        }
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.post(route.url)
                    .map {
                        if (it.isSuccessful && it.body() != null && it.body()!!.size() > 0) {
                            val response = gson.fromJson(it.body(), clazz)
                            cacheData(cacheValue.copy(response))
                            return@map response
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> post(
        routeCode: String,
        params: Map<String, Any>,
        clazz: Class<T>
    ): Observable<T> {
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.post(route.url, params)
                    .map {
                        if (it.isSuccessful && it.body() != null) {
                            return@map gson.fromJson(it.body(), clazz)
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    override fun <T : Any> postUrl(
        url: String,
        params: Map<String, Any>,
        clazz: Class<T>
    ): Observable<T> {
        return apiEndpoint.post(url, params)
            .map {
                if (it.isSuccessful && it.body() != null) {
                    return@map gson.fromJson(it.body(), clazz)
                }
                throw DataNotFoundException()
            }
    }

    override fun <T : Any> post(
        routeCode: String,
        params: Map<String, Any>,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T> {
        val cachedData = filterDataInCache(cacheValue).value
        if (cachedData != null) {
            return Observable.just(cachedData)
        }
        return apiRouteProvider.getUrl(routeCode)
            .take(1)
            .flatMap { route ->
                apiEndpoint.post(route.url, params)
                    .map {
                        if (it.isSuccessful && it.body() != null && it.body()!!.size() > 0) {
                            val response = gson.fromJson(it.body(), clazz)
                            cacheData(cacheValue.copy(response))
                            return@map response
                        }
                        throw DataNotFoundException()
                    }
            }
    }

    private fun <T : Any> filterDataInCache(cache: CacheValue<T>): CacheValue<T> {
        if (cache.shouldCache && cache.key.toString().isNotEmpty()
            && cache.cacheType != CacheType.CACHE_TYPE_NONE && cache.retrieveCache
        ) {
            val cacheValue = cacheManagerProvider.getCacheManager(cache.cacheType.name)
                .queryData<T>(cache.key).value

            if (cacheValue != null) {
                return CacheValue(cacheValue)
            }
            return CacheValue(null)
        }
        return CacheValue(null)
    }

    private fun <T : Any> cacheData(cache: CacheValue<T>) {
        if (cache.value != null && cache.shouldCache && cache.key.toString().isNotEmpty()
            && cache.cacheType != CacheType.CACHE_TYPE_NONE
        ) {
            cacheManagerProvider.getCacheManager(cache.cacheType.name)
                .insertData(cache.key, cache)
        }
    }
}