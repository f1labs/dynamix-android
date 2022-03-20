package com.dynamix.core.extensions

import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

/**
 * Created by user on 10/11/2021.
 */


/**
 * parses the throwable to get appropriate error message, mostly desinged to be used in data or domain layer
 */
fun Throwable.getParsedThrowable(): Throwable {
    return Throwable(parseMessage())
}

fun Throwable.parseMessage(): String {
    return when (this) {
        is HttpException -> {
            when (this.code()) {
                in 400..499 -> {
                    "Service not available, Try Again"
                }

                in 500..599 -> {
                    "Error processing request, Try Again"
                }

                else -> {
                    "Something went wrong, Try Again"
                }
            }
        }

        is SocketTimeoutException -> {
            "Cannot connect to Server, Try Again"
        }

        is UnknownHostException -> {
            "No internet connection to Server"
        }

        is MalformedJsonException -> {
            "Failed to parse Server response"
        }

        else -> {
            "Something went wrong, Try Again"
        }
    }
}

/**
 * [String] extensions
 */

fun String.toCapitalize(): String =
    replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }

fun String.capitalizeAll(): String = split(" ")
    .joinToString(" ") { it.lowercase().toCapitalize() }

