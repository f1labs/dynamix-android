package com.dynamix.core.logger

import com.dynamix.core.BuildConfig
import timber.log.Timber

class AppLoggerProviderImpl : AppLoggerProvider {

    private val TAG = "APP_LOGGER"

    override fun debug(log: String) {
        println("Debug ::: $log")
        if (!BuildConfig.DEBUG) {
            Timber.d(TAG + log)
        }
    }

    override fun verbose(log: String?) {
        Timber.v(TAG + log!!)
    }

    override fun error(log: String) {
        Timber.e("$TAG Caught Exception $log")
    }

    override fun info(log: String?) {
        Timber.i("$TAG $log")
    }

    override fun warning(log: String?) {
        Timber.w("$TAG Caught Exception $log")
    }

    override fun error(t: Throwable) {
        Timber.e(t)
    }

    override fun setUserIdentifier(identifier: String?) {
        Timber.i(identifier)
    }
}