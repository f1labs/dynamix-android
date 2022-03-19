package com.dynamix.modsign.model

data class ModSignVisibility(
    val operator: String? = null,
    val conditions: List<ModSignVisibility>? = ArrayList(),
    val key: String? = "",
    val value: String? = "",
    val condition: String? = "=",
    val compareType: String? = "String"
)
