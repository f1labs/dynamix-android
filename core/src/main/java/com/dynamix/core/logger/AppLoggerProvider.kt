package com.dynamix.core.logger

interface AppLoggerProvider {

    fun debug(log: String)

    fun verbose(log: String?)

    fun error(log: String)

    fun info(log: String?)

    fun warning(log: String?)

    fun error(t: Throwable)

    fun setUserIdentifier(identifier: String?)
}