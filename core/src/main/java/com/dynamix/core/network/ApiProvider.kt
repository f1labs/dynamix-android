package com.dynamix.core.network

import com.dynamix.core.cache.CacheValue
import io.reactivex.Observable

interface ApiProvider {

    fun <T : Any> get(routeCode: String, clazz: Class<T>): Observable<T>

    fun <T : Any> get(routeCode: String, clazz: Class<T>, cacheValue: CacheValue<T>): Observable<T>

    fun <T : Any> getUrl(url: String, clazz: Class<T>): Observable<T>

    fun <T : Any> getUrl(url: String, clazz: Class<T>, cacheValue: CacheValue<T>): Observable<T>

    fun <T : Any> get(routeCode: String, path: String, clazz: Class<T>): Observable<T>

    fun <T : Any> get(routeCode: String, path: String, clazz: Class<T>, cacheValue: CacheValue<T>): Observable<T>

    fun <T : Any> get(routeCode: String, params: Map<String, Any>, clazz: Class<T>): Observable<T>

    fun <T : Any> get(
        routeCode: String,
        params: Map<String, Any>,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T>

    fun <T : Any> post(routeCode: String, clazz: Class<T>): Observable<T>

    fun <T : Any> postUrl(url: String, clazz: Class<T>): Observable<T>

    fun <T : Any> post(routeCode: String, clazz: Class<T>, cacheValue: CacheValue<T>): Observable<T>

    fun <T : Any> post(routeCode: String, params: Map<String, Any>, clazz: Class<T>): Observable<T>

    fun <T : Any> postUrl(url: String, params: Map<String, Any>, clazz: Class<T>): Observable<T>

    fun <T : Any> post(
        routeCode: String,
        params: Map<String, Any>,
        clazz: Class<T>,
        cacheValue: CacheValue<T>
    ): Observable<T>
}