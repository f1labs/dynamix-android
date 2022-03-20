package com.dynamix.modsign.core.events

import com.dynamix.modsign.core.components.recyclerview.RvAdapter

interface DynamixRvEvent {
    fun onAdapterSet(adapter: RvAdapter)
}