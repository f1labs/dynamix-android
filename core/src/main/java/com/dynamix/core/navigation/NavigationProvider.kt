package com.dynamix.core.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dynamix.core.event.DynamixEvent

interface NavigationProvider {

    fun init(data: Bundle): NavigationProvider

    fun upToNoHistory(action: Class<out AppCompatActivity>?)

    fun upTo(action: Class<out AppCompatActivity>?)

    fun upToWithResult(action: Class<out AppCompatActivity>?, requestCode: Int)

    fun upToClearTask(action: Class<out AppCompatActivity>?)

    fun navigate(navigator: Navigator)

    fun navigate(navigator: Navigator, data: Map<String, Any> = emptyMap())

    fun navigate(event: DynamixEvent, data: Map<String, Any> = emptyMap())
}