package com.dynamix.core.cache

class AppEnvironment {

    private val environmentValues: MutableMap<String, Any> = HashMap()

    fun insertOrUpdate(key: String, value: Any) {
        environmentValues[key] = value
    }

    fun get(key: String): Any {
        return if(environmentValues.containsKey(key)) {
            environmentValues[key]!!
        } else {
            ""
        }
    }

    companion object {
        private var appEnvironment: AppEnvironment? = null

        val instance: AppEnvironment
            get() {
                if (appEnvironment == null) appEnvironment = AppEnvironment()
                return appEnvironment!!
            }
    }

}