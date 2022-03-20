package com.dynamix.modsign

import com.dynamix.modsign.view.ModSignDataProvider
import com.dynamix.modsign.view.ModSignDataProviderImpl
import com.dynamix.modsign.view.ModSignVm
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun modSign() = module {

    single<ModSignDataProvider> {
        ModSignDataProviderImpl(get(), get(), get(), get())
    }

    viewModel {
        ModSignVm(get(), get())
    }
}