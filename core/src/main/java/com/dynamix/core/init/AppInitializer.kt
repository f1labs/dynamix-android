package com.dynamix.core.init

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dynamix.core.navigation.NavigationComponents
import timber.log.Timber

object AppInitializer {

    var urlMap: Map<String, String> = HashMap()

    // This context is application context and be careful to use it.
    fun init(context: Context) {
        Timber.plant(Timber.DebugTree())
        DynamixEnvironmentData.FILE_PROVIDER = ".provider"
    }

    fun registerActivities(activityMap: Map<String, Class<out AppCompatActivity>>) {
        NavigationComponents.registerActivities(activityMap.toMutableMap())
    }

    fun registerFragments(fragmentMap: Map<String, Class<out Fragment>>) {
        NavigationComponents.registerFragments(fragmentMap)
    }
}