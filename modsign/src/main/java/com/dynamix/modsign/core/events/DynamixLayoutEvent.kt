package com.dynamix.modsign.core.events

interface DynamixLayoutEvent {
    fun onViewInflated(viewData: HashMap<String, Any>)
}