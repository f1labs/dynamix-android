package com.dynamix.core.network

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response

class ApiEndPointTester : ApiEndpoint {

    override fun get(url: String): Observable<Response<JsonObject>> {
        TODO("Not yet implemented")
    }

    override fun get(url: String, params: Map<String, Any>): Observable<Response<JsonObject>> {
        TODO("Not yet implemented")
    }

    override fun post(url: String): Observable<Response<JsonObject>> {
        TODO("Not yet implemented")
    }

    override fun post(url: String, params: Map<String, Any>): Observable<Response<JsonObject>> {
        TODO("Not yet implemented")
    }
}