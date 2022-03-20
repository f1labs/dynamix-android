package com.dynamix.modsign

import com.dynamix.modsign.view.ModSignVm
import org.koin.java.KoinJavaComponent.inject

open class ModSignController {

    lateinit var stylesMap: Map<*, *>
    private val modSignVm: ModSignVm by inject(ModSignVm::class.java)

    fun onCreate() {
        loadStyles()
    }

    open fun loadStyles() {
        modSignVm.loadStyles {
            stylesMap = it
        }
    }

    companion object {
        private var appController: ModSignController? = null

        val instance: ModSignController
            get() {
                if (appController == null) appController = ModSignController()
                return appController!!
            }
    }
}