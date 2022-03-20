package com.dynamix.core.network

data class ApiRoute(
    val url: String = "",
    val code: String? = null,
    val isProtected: Boolean = false,
    val ignoredUrl: Boolean = false
)