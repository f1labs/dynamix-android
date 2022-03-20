package com.dynamix.modsign.view

import android.annotation.SuppressLint
import com.dynamix.core.cache.CacheType
import com.dynamix.core.cache.CacheValue
import com.dynamix.core.cache.provider.PermanentGroupCacheProvider
import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.network.ApiProvider
import com.dynamix.core.network.DynamixApi
import com.dynamix.core.network.DynamixInitialDataApi
import com.dynamix.modsign.ModSignCacheConfigs
import com.dynamix.modsign.ModSignKeyConfigs
import com.dynamix.modsign.ModsignConfigurations
import com.dynamix.modsign.model.LayoutWrapper
import com.dynamix.modsign.model.ModSignVersion
import com.dynamix.modsign.model.RootView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class ModSignDataProviderImpl(
    private val apiProvider: ApiProvider,
    private val gson: Gson,
    private val permanentGroupCacheProvider: PermanentGroupCacheProvider,
    private val appLoggerProvider: AppLoggerProvider
) : ModSignDataProvider {

    @SuppressLint("CheckResult")
    override fun invalidateCacheIfRequired() {
        apiProvider.get(ModSignKeyConfigs.MODSIGN_VERSION_DATA, ModSignVersion::class.java)
            .subscribeOn(Schedulers.io())
            .subscribe({
                var localVersion = -1
                val localVersionJson =
                    permanentGroupCacheProvider.query<JsonObject>(ModSignCacheConfigs.MOD_SIGN_VERSION)
                val modSignVersion =
                    gson.fromJson(localVersionJson.value.toString(), ModSignVersion::class.java)

                if (modSignVersion != null) {
                    localVersion = modSignVersion.version
                }

                if (it.version > localVersion) {
                    appLoggerProvider.info("ModSign cache data clearing")
                    permanentGroupCacheProvider.removeGroup(ModSignCacheConfigs.MOD_SIGN_GROUP_KEY)

                    permanentGroupCacheProvider.insert(
                        ModSignCacheConfigs.MOD_SIGN_VERSION,
                        CacheValue(it)
                    )
                }
            }, {
                appLoggerProvider.error(it)
            })
    }

    override fun loadParsedStyles(): Observable<Map<String, Any>> {
        return Observable.just(
            permanentGroupCacheProvider.query<JsonObject>(
                ModSignCacheConfigs.MOD_SIGN_CACHE_COMPILED_STYLES_DATA
            )
        ).onErrorReturn { CacheValue(value = null) }.flatMap {
            if (it.value != null && it.value.toString().isNotEmpty()) {
                appLoggerProvider.debug("Cached parsed styles data")
                return@flatMap gson.fromJson(
                    it.toString(), object : TypeToken<HashMap<String, Any>>() {}.type
                )
            }
            appLoggerProvider.debug("Styles data from data source")
            return@flatMap Observable.zip(
                loadStylesData(),
                loadVariables()
            ) { styles, variables ->
                for ((key, value) in styles) {
                    for ((key1, value1) in value as LinkedTreeMap<String, String>) {
                        if (value1.startsWith("$")) {
                            (styles[key] as LinkedTreeMap<String, String>)[key1] =
                                variables[value1.drop(1)] as String
                        }
                    }
                }
                val stylesJson: JsonObject =
                    gson.fromJson(gson.toJson(styles), JsonObject::class.java)
                permanentGroupCacheProvider.insert(
                    ModSignCacheConfigs.MOD_SIGN_CACHE_COMPILED_STYLES_DATA,
                    CacheValue(
                        value = stylesJson,
                        groupKey = ModSignCacheConfigs.MOD_SIGN_GROUP_KEY,
                        key = ModSignCacheConfigs.MOD_SIGN_CACHE_COMPILED_STYLES_DATA,
                        cacheType = CacheType.CACHE_TYPE_PERMANENT_GROUP
                    )
                )
                styles
            }
        }
    }

    private fun loadStylesData(): Observable<Map<String, Any>> {
        return apiProvider.get(
            ModSignKeyConfigs.MODSIGN_STYLES_DATA, JsonObject::class.java,
            CacheValue(
                value = JsonObject(),
                groupKey = ModSignCacheConfigs.MOD_SIGN_GROUP_KEY,
                key = ModSignCacheConfigs.MOD_SIGN_CACHE_STYLES_DATA,
                cacheType = CacheType.CACHE_TYPE_PERMANENT_GROUP
            )
        ).onErrorReturn { JsonObject() }
            .map {
                return@map gson.fromJson(
                    it.toString(), object : TypeToken<HashMap<String, Any>>() {}.type
                )
            }
    }

    private fun loadVariables(): Observable<Map<String, Any>> {
        return apiProvider.get(
            ModSignKeyConfigs.MODSIGN_VARIABLES, JsonObject::class.java,
            CacheValue(
                value = JsonObject(),
                groupKey = ModSignCacheConfigs.MOD_SIGN_GROUP_KEY,
                key = ModSignCacheConfigs.MOD_SIGN_CACHE_VARIABLES_DATA,
                cacheType = CacheType.CACHE_TYPE_PERMANENT_GROUP
            )
        ).map {
            return@map gson.fromJson(
                it.toString(), object : TypeToken<HashMap<String, Any>>() {}.type
            )
        }
    }

    override fun loadLayout(layoutUrl: String, event: DynamixEvent): Observable<LayoutWrapper> {
        return apiProvider.getUrl(
            layoutUrl, JsonObject::class.java, CacheValue(
                value = JsonObject(),
                groupKey = ModSignCacheConfigs.MOD_SIGN_GROUP_KEY,
                key = layoutUrl,
                cacheType = CacheType.CACHE_TYPE_PERMANENT_GROUP,
                //retrieveCache = false
            )
        ).onErrorReturn { JsonObject() }
            .map {
                return@map gson.fromJson(it.toString(), LayoutWrapper::class.java)
            }
            .flatMap { return@flatMap adaptStoredData(it, event) }
    }

    override fun loadLayoutWithLayoutCode(
        layoutCode: String,
        event: DynamixEvent
    ): Observable<LayoutWrapper> {
        return apiProvider.get(
            ModSignKeyConfigs.MODSIGN_INITIAL_DATA, JsonObject::class.java,
            CacheValue(
                value = JsonObject(),
                groupKey = ModSignCacheConfigs.MOD_SIGN_GROUP_KEY,
                key = ModSignCacheConfigs.MOD_SIGN_CACHE_INITIAL_DATA,
                cacheType = CacheType.CACHE_TYPE_PERMANENT_GROUP
            )
        ).map { json ->
            return@map gson.fromJson(json.toString(), DynamixInitialDataApi::class.java)
        }.flatMap {
            if (it.isSuccess && !it.apis.isNullOrEmpty()) {
                return@flatMap apiProvider.getUrl(
                    getUrlFromInitialData(layoutCode, it.apis),
                    JsonObject::class.java,
                    CacheValue(
                        JsonObject(),
                        key = layoutCode,
                        groupKey = ModSignCacheConfigs.MOD_SIGN_GROUP_KEY,
                        cacheType = CacheType.CACHE_TYPE_PERMANENT_GROUP
                    )
                ).onErrorReturn { JsonObject() }
                    .map { layoutJson ->
                        return@map gson.fromJson(layoutJson.toString(), LayoutWrapper::class.java)
                    }
                    .flatMap { layoutWrapper ->
                        return@flatMap adaptStoredData(
                            layoutWrapper,
                            event
                        )
                    }
            }
            return@flatMap Observable.just(LayoutWrapper())
        }
    }

    private fun getUrlFromInitialData(layoutCode: String, urlList: List<DynamixApi>): String {
        for ((apiCode, url) in urlList) {
            if (apiCode == layoutCode && url!!.isNotEmpty()) {
                return getDataUrl(url)
            }
        }
        return ""
    }

    private fun getDataUrl(url: String): String {
        var finalUrl = url

        val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
        val matcher = pattern.matcher(url)
        while (matcher.find()) {
            finalUrl = finalUrl.replace(
                "{{" + matcher.group(1) + "}}",
                ModsignConfigurations.urlMap[matcher.group(1)] as String,
                true
            )
        }
        return finalUrl
    }

    private fun adaptStoredData(
        layout: LayoutWrapper,
        event: DynamixEvent
    ): Observable<LayoutWrapper> {
        if (event.storageId.isNotEmpty()) {
            var filteredData = layout
            val rootView = filteredData.layout

            if (rootView?.getDataUrl() != null && rootView.getDataUrl()!!.equals(
                    ModSignKeyConfigs.ADAPT_STORAGE_DATA,
                    ignoreCase = true
                )
            ) {
                rootView.setDataUrl("${ModSignKeyConfigs.ADAPT_STORAGE_DATA}${event.storageId}")
            }
            val viewChildren: MutableList<RootView> = mutableListOf()
            rootView?.children!!.forEach {
                val view = it
                if (!view.getDataUrl().isNullOrEmpty() && view.getDataUrl()!!.equals(
                        ModSignKeyConfigs.ADAPT_STORAGE_DATA,
                        ignoreCase = true
                    )
                ) {
                    view.setDataUrl(
                        "${ModSignKeyConfigs.ADAPT_STORAGE_DATA}${event.storageId}"
                    )
                }
                viewChildren.add(view)
            }
            filteredData = filteredData.copy(
                layout = rootView.copy(
                    children = viewChildren
                )
            )
            return Observable.just(filteredData)
        }
        return Observable.just(layout)
    }
}