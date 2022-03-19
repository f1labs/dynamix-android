package com.dynamix.core.network

import com.google.gson.annotations.SerializedName

data class DynamixApi(
    val code: String? = null,
    val url: String? = null
)

data class DynamixInitialDataApi(
    @SerializedName("success")
    val isSuccess: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    val apis: List<DynamixApi> = listOf()
)