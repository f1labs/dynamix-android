package com.dynamix.modsign.model

data class Background(
    val width: Int = -9,
    val height: Int = -9,
    val type: String? = "",
    val shape: String? = "",
    val cornerRadius: Float = 0f,
    val borderWidth: Int = -9,
    val borderColor: String? = "",
    val backgroundColor: String? = "",
    val startColor: String? = "",
    val centerColor: String? = "",
    val endColor: String? = "",
    val angle: Int = -9
)