package com.dynamix.core.cache

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.koin.java.KoinJavaComponent.inject

class DynamixDataStorage {

    private val gson: Gson by inject(Gson::class.java)

    private var IDList: MutableSet<String> = mutableSetOf()
    private var storedDataMap: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()

    fun generateID(): String {
        val id = System.currentTimeMillis().toString()
        IDList.add(id)
        return id
    }

    fun storeData(key: String, dataToStore: JsonObject) {
        val dataToStoreAsMap: Map<String, Any> = gson.fromJson(
            dataToStore.toString(), object : TypeToken<HashMap<String?, Any?>?>() {}.type
        )
        if (storedDataMap.containsKey(key)) {
            storedDataMap[key]?.putAll(dataToStoreAsMap)
        } else {
            storedDataMap[key] = dataToStoreAsMap.toMutableMap()
        }
    }

    fun storeData(key: String, dataToStore: Map<String, Any>) {
        if (storedDataMap.containsKey(key)) {
            storedDataMap[key]?.putAll(dataToStore)
        } else {
            storedDataMap[key] = dataToStore.toMutableMap()
        }
    }

    fun getStoredData(key: String, paramKeys: List<String> = emptyList()): Map<String, Any> {
        if (IDList.contains(key) && storedDataMap.containsKey(key)) {
            val storedData: Map<String, Any> = storedDataMap[key]!!

            if (paramKeys.isEmpty()) {
                return storedData
            }
            val paramsForApiCall: MutableMap<String, Any> = mutableMapOf()
            paramKeys.forEach { filterKey ->
                if(storedData.containsKey(filterKey)) {
                    paramsForApiCall[filterKey] = storedData[filterKey]!!
                }
            }
            return paramsForApiCall
        }
        return emptyMap()
    }

    fun clearData(key: String) {
        if (IDList.contains(key)) {
            IDList.remove(key)
        }
        if (storedDataMap.containsKey(key)) {
            storedDataMap.remove(key)
        }
    }

    fun clearAll() {
        IDList = mutableSetOf()
        storedDataMap = mutableMapOf()
    }
}