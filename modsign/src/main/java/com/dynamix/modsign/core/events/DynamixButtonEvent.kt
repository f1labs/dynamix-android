package com.dynamix.modsign.core.events

import com.dynamix.modsign.model.RootView

interface DynamixButtonEvent {

    fun onButtonClicked(view: RootView)
}