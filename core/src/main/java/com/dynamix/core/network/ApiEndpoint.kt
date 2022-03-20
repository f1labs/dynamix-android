package com.dynamix.core.network

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiEndpoint {

    @GET
    @Headers("Content-Type: application/json")
    fun get(@Url url: String): Observable<Response<JsonObject>>

    @GET
    @Headers("Content-Type: application/json")
    fun get(
        @Url url: String,
        @Body params: Map<String, @JvmSuppressWildcards Any>
    ): Observable<Response<JsonObject>>

    @POST
    @Headers("Content-Type: application/json")
    fun post(@Url url: String): Observable<Response<JsonObject>>

    @POST
    @Headers("Content-Type: application/json")
    fun post(
        @Url url: String,
        @Body params: Map<String, @JvmSuppressWildcards Any>
    ): Observable<Response<JsonObject>>
}