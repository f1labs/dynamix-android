package com.dynamix.core.gate

import androidx.collection.SimpleArrayMap


internal class DynamixGateMapper {

    private var gateTypes: SimpleArrayMap<String, DynamixGateHandler> = SimpleArrayMap()

    fun addGate(type: String, gate: DynamixGateHandler) {
        gateTypes.put(type, gate)
    }

    fun handler(type: String): DynamixGateHandler? {
        if (gateTypes.containsKey(type)) {
            return gateTypes[type]!!
        }
        return null
    }
}