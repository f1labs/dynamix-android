package com.dynamix.core.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dynamix.core.view.DynamixContainerActivity
import com.dynamix.core.view.DynamixWebViewActivity
import org.apache.commons.lang3.mutable.Mutable

object NavigationComponents {

    private var activityMap: Map<String, Class<out AppCompatActivity>> = mapOf()
    private var fragmentMap: Map<String, Class<out Fragment>> = mapOf()

    fun registerActivities(activityMap: MutableMap<String, Class<out AppCompatActivity>>) {
        activityMap.apply {
            put(NavigationConstants.DYNAMIX_CONTAINER_ACTIVITY, DynamixContainerActivity::class.java)
//            put(NavigationConstants.DYNAMIX_WEB_VIEW_ACTIVITY, DynamixWebViewActivity::class.java)
        }
        NavigationComponents.activityMap = activityMap
    }

    fun registerFragments(fragmentMap: Map<String, Class<out Fragment>>) {
        NavigationComponents.fragmentMap = fragmentMap
    }

    fun getActivityFromCode(code: String): Class<out AppCompatActivity>? {
        return activityMap[code]
    }

    fun getFragmentFromCode(code: String): Class<out Fragment>? {
        return fragmentMap[code]
    }
}