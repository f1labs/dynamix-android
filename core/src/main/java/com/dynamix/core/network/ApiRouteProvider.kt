package com.dynamix.core.network

import io.reactivex.Observable

open class ApiRouteProvider {

    // API will be managed stores in this map on basis of key
    open val routes: MutableMap<String, ApiRoute> = mutableMapOf()

    fun getUrl(code: String): Observable<ApiRoute> {
        if (routes.isEmpty() || !routes.containsKey(code)) {
            throw IllegalArgumentException("Route not defined")
        }
        return Observable.just(routes[code])
    }

    fun getRoute(code: String): ApiRoute {
        if (routes.isEmpty() || !routes.containsKey(code)) {
            return ApiRoute("", "")
        }
        return routes[code]!!
    }

    fun publicRoutes(): List<ApiRoute> {
        val publicRoutes: MutableList<ApiRoute> = mutableListOf()
        routes.values.filter { !it.isProtected }
            .toCollection(publicRoutes)
        return publicRoutes
    }

    fun ignoredRoutes(): List<ApiRoute> {
        val ignoredRoutes: MutableList<ApiRoute> = mutableListOf()
        routes.values.filter { it.ignoredUrl }
            .toCollection(ignoredRoutes)
        return ignoredRoutes
    }
}