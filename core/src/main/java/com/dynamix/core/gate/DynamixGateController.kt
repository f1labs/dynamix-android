package com.dynamix.core.gate

import android.content.Context

object DynamixGateController {

    private val gateMapper = DynamixGateMapper()

    fun init(): DynamixGateController {
        return this
    }

    fun addGate(type: String, gate: DynamixGateHandler): DynamixGateController {
        gateMapper.addGate(type, gate)
        return this
    }

    fun handleGate(context: Context, gate: DynamixGate) {
        if (gateMapper.handler(gate.type) != null) {
            gateMapper.handler(gate.type)!!.handle(context, gate)
        } else {
            println("Gate Type Not Found::: ${gate.type}")
        }
    }
}