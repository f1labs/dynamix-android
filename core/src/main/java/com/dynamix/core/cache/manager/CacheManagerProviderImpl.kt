package com.dynamix.core.cache.manager

import com.dynamix.core.cache.CacheType
import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.provider.*
import com.dynamix.core.logger.LoggerProviderUtils

class CacheManagerProviderImpl(
    private val applicationDataCacheProvider: ApplicationDataCacheProvider,
    private val intermediateDataCacheProvider: IntermediateDataCacheProvider,
    private val permanentCacheProvider: PermanentCacheProvider,
    private val permanentGroupCacheProvider: PermanentGroupCacheProvider,
    private val sessionCacheProvider: SessionCacheProvider
) : CacheManagerProvider {

    private lateinit var provider: CacheProvider

    private fun initProvider(cacheType: String) {
        provider = when (cacheType) {
            CacheType.CACHE_TYPE_APPLICATION_DATA.name -> applicationDataCacheProvider
            CacheType.CACHE_TYPE_INTERMEDIATE_DATA.name -> intermediateDataCacheProvider
            CacheType.CACHE_TYPE_PERMANENT.name -> permanentCacheProvider
            CacheType.CACHE_TYPE_PERMANENT_GROUP.name -> permanentGroupCacheProvider
            CacheType.CACHE_TYPE_SESSION_DATA.name -> sessionCacheProvider
            else -> {
                sessionCacheProvider
            }
        }
    }

    override fun getCacheManager(cacheType: String): CacheManagerProvider {
        initProvider(cacheType)
        return this
    }

    override fun getProvider(cacheType: String): CacheProvider {
        initProvider(cacheType)
        return provider
    }

    override fun <T : Any> insertData(key: Any, value: CacheValue<T>) {
        if (provider is SessionCacheProvider) {
            (provider as SessionCacheProvider).insertOrUpdate(key, value)
        } else {
            provider.insert(key, value)
        }
    }

    override fun <T : Any> queryData(key: Any): CacheValue<T> {
        return provider.query(key)
    }

    override fun removeData(key: Any) {
        provider.remove(key)
    }

    override fun removeGroup(groupKey: String) {
        if (provider is PermanentGroupCacheProvider) {
            (provider as PermanentGroupCacheProvider).removeGroup(groupKey)
        } else {
            LoggerProviderUtils.warning("No providers found to remove group data")
        }
    }

    override fun clearAll() {
        provider.clear()
    }
}