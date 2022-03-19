package com.dynamix.core.utils

interface Base64Provider {

    fun encode(s: String): String

    fun encode(src: ByteArray, start: Int = 0, length: Int = src.size): String

    fun decode(s: String): ByteArray
}