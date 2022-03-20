package com.dynamix.core.cache.impl

import android.content.SharedPreferences
import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.provider.PermanentCacheProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class PermanentCacheProviderImpl(
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences
) : CacheImpl(), PermanentCacheProvider {

    override fun <T : Any> insert(key: Any, value: CacheValue<T>) {
        val jsonString = gson.toJson(value)
        sharedPreferences.edit().putString(key.toString(), jsonString).apply()
    }

    override fun <T : Any> query(key: Any): CacheValue<T> {
        if (sharedPreferences.contains(key.toString())) {
            val jsonString = sharedPreferences.getString(key.toString(), null)
            return if (jsonString != null) {
                val cacheValue = gson.fromJson(jsonString, CacheValue::class.java)
                val dataTreeMap: T = gson.fromJson(gson.toJson(cacheValue.value), object : TypeToken<T>() {}.type)
                val json = gson.fromJson(gson.toJson(dataTreeMap), JsonObject::class.java) as T
                return CacheValue(json)
            } else {
                // remove the cache entry
                remove(key)
                CacheValue(null)
            }
        }
        return CacheValue(null)
    }

    override fun remove(key: Any) {
        sharedPreferences.edit().putString(key.toString(), null).apply()
    }

    override fun clear() {

    }
}