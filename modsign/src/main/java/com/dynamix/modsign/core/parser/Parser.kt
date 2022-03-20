package com.dynamix.modsign.core.parser

import android.view.View
import com.dynamix.modsign.model.RootView

interface Parser {
    fun parse(rootView: RootView): View?
}