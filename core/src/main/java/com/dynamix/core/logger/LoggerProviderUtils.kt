package com.dynamix.core.logger

import com.dynamix.core.BuildConfig
import timber.log.Timber

object LoggerProviderUtils {

    private val TAG = "APP_LOGGER"

    fun debug(log: String) {
        println("Debug ::: $log")
        if (!BuildConfig.DEBUG) {
            Timber.d(TAG, log)
        }
    }

    fun verbose(log: String?) {
        Timber.v(TAG, log!!)
    }

    fun error(log: String) {
        Timber.e(TAG + "Caught Exception " + log)
    }

    fun info(log: String?) {
        Timber.i(TAG, log ?: "No message available")
    }

    fun warning(log: String?) {
        Timber.w(TAG + "Caught Exception " + log)
    }

    fun error(t: Throwable) {
        Timber.e(t)
    }

    fun setUserIdentifier(identifier: String?) {
        Timber.i(identifier)
    }
}