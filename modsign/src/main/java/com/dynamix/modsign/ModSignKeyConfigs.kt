package com.dynamix.modsign

object ModSignKeyConfigs {

    //For API calls
    const val MODSIGN_INITIAL_DATA = "MODSIGN_INITIAL_DATA"
    const val MODSIGN_STYLES_DATA = "MODSIGN_STYLES_DATA"
    const val MODSIGN_VARIABLES = "MODSIGN_VARIABLES"

    //For Data Cache
    const val CACHE_STYLES_DATA = "CACHE_STYLES_DATA"
    const val CACHE_VARIABLES_DATA = "CACHE_VARIABLES_DATA"
    const val CACHE_INITIAL_DATA = "CACHE_INITIAL_DATA"

    const val ADAPT_STORAGE_DATA = "localdata"

    const val GATE_TYPE = "gateType"
}

object ModSignCacheConfigs {
    const val MOD_SIGN_GROUP_KEY = "MOD_SIGN_GROUP_KEY"
    const val MOD_SIGN_VERSION = "MOD_SIGN_VERSION"

    const val MOD_SIGN_CACHE_STYLES_DATA = "MOD_SIGN_CACHE_STYLES_DATA"
    const val MOD_SIGN_CACHE_INITIAL_DATA = "MOD_SIGN_CACHE_INITIAL_DATA"
}

/*
ModSign Initialize ->> Api for cache version and seeks for version in cache,
if remove version > localVersion clear allCaches

           groupKey ->> Key for particular cache, value for particular cache
Cache ->>> Key ->> Key, Value
           ModSign ->> InitialData, InitialData::class.java

 */