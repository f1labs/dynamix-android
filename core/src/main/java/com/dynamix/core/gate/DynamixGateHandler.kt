package com.dynamix.core.gate

import android.content.Context

interface DynamixGateHandler {

    fun handle(context: Context, gate: DynamixGate)
}