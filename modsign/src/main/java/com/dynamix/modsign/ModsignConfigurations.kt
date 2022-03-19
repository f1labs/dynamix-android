package com.dynamix.modsign

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dynamix.core.navigation.NavigationComponents
import com.dynamix.core.navigation.NavigationConstants
import com.dynamix.modsign.view.ModSignActivity
import com.dynamix.modsign.view.ModSignDataProvider
import org.koin.java.KoinJavaComponent.inject

object ModsignConfigurations {

    private val modSignDataProvider: ModSignDataProvider by inject(ModSignDataProvider::class.java)
    var urlMap: Map<String, String> = HashMap()

    // This context is application context and be careful to use it.
    fun init(context: Context): ModsignConfigurations {
        return this
    }

    fun setUrls(_urlMap: Map<String, String>): ModsignConfigurations {
        urlMap = _urlMap
        return this
    }

    fun invalidateCacheIfRequired() {
        modSignDataProvider.invalidateCacheIfRequired()
    }

    fun registerActivities(activityMap: MutableMap<String, Class<out AppCompatActivity>>) {
        activityMap.apply {
            put(NavigationConstants.MODSIGN_ACTIVITY, ModSignActivity::class.java)
        }
        NavigationComponents.registerActivities(activityMap)
    }

    fun registerFragments(fragmentMap: Map<String, Class<out Fragment>>) {
        NavigationComponents.registerFragments(fragmentMap)
    }
}