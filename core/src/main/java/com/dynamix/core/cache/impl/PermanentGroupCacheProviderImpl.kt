package com.dynamix.core.cache.impl

import android.content.SharedPreferences
import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.provider.PermanentGroupCacheProvider
import com.dynamix.core.logger.LoggerProviderUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PermanentGroupCacheProviderImpl(
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences
) : CacheImpl(), PermanentGroupCacheProvider {

    private val PERMANENT_GROUP_CACHE_PROVIDER = "PERMANENT_GROUP_CACHE_PROVIDER"

    //Store whole object
    override fun <T : Any> insert(key: Any, value: CacheValue<T>) {
        val jsonString = gson.toJson(value)
        sharedPreferences.edit().putString(key.toString(), jsonString).apply()

        val keys: MutableSet<String>? =
            sharedPreferences.getStringSet(PERMANENT_GROUP_CACHE_PROVIDER, mutableSetOf())
        val allKeySets: MutableSet<String> = mutableSetOf()
        if (keys != null) {
            allKeySets.addAll(keys.toMutableSet())
            allKeySets.add(key.toString())
        } else {
            allKeySets.add(key.toString())
        }
        sharedPreferences.edit().putStringSet(PERMANENT_GROUP_CACHE_PROVIDER, allKeySets).apply()
    }

    //Search for value based on key and return that
    override fun <T : Any> query(key: Any): CacheValue<T> {
        if (sharedPreferences.contains(key.toString())) {
            val jsonString = sharedPreferences.getString(key.toString(), null)
            return if (jsonString != null) {
                val cacheValue = gson.fromJson(jsonString, CacheValue::class.java)
                val dataTreeMap: T =
                    gson.fromJson(gson.toJson(cacheValue.value), object : TypeToken<T>() {}.type)
                val json = gson.fromJson(gson.toJson(dataTreeMap), JsonObject::class.java) as T
                return CacheValue(json)
            } else {
                remove(key)
                CacheValue(null)
            }
        }
        return CacheValue(null)
    }

    override fun remove(key: Any) {
        for ((preferenceKey, preferenceValue) in sharedPreferences.all) {
            if (preferenceValue is String) {
                try {
                    val cacheValue = gson.fromJson(preferenceValue, CacheValue::class.java)
                    if (cacheValue.groupKey.toString().isNotEmpty() &&
                        preferenceKey.equals(key.toString(), ignoreCase = true)
                    ) {
                        sharedPreferences.edit().putString(key.toString(), null).apply()
                    }
                } catch (ex: JsonSyntaxException) {
                    LoggerProviderUtils.error(ex)
                }
            }
        }
    }

    // Scan all preferences and if value contains group key and
    // groupKey of that particular preferences matches with groupKey provided,
    // remove that key from the cache
    override fun removeGroup(groupKey: String) {
        for ((preferenceKey, preferenceValue) in sharedPreferences.all) {
            if (preferenceValue is String && preferenceValue.toString().isValidJsonObject()) {
                val cacheValue = gson.fromJson(preferenceValue, CacheValue::class.java)
                if (cacheValue.groupKey != null &&
                    cacheValue.groupKey.toString().isNotEmpty() &&
                    cacheValue.groupKey.toString().equals(groupKey, ignoreCase = true)
                ) {
                    println("Clearing mod sign data $preferenceKey")
                    sharedPreferences.edit().putString(preferenceKey, null).apply()
                }
            }
        }
    }

    private fun String?.isValidJsonObject(): Boolean {
        return isJsonObject(this)
    }

    private fun String?.isValidJsonArray(): Boolean {
        return isJsonArray(this)
    }

    private fun isJsonObject(jsonString: String?): Boolean {
        try {
            JSONObject(jsonString!!)
        } catch (ex: JSONException) {
            return false
        }
        return true
    }

    private fun isJsonArray(jsonString: String?): Boolean {
        try {
            JSONArray(jsonString!!)
        } catch (ex: JSONException) {
            return false
        }
        return true
    }

    override fun clear() {
        val keys: MutableSet<String>? =
            sharedPreferences.getStringSet(PERMANENT_GROUP_CACHE_PROVIDER, mutableSetOf())
        keys?.forEach {
            remove(it)
            sharedPreferences.edit().putString(it, null).apply()
        }
    }
}
