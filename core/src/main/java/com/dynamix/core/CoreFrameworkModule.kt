package com.dynamix.core

import android.app.Application
import com.dynamix.core.cache.DynamixDataStorage
import com.dynamix.core.cache.impl.*
import com.dynamix.core.cache.manager.CacheManagerProvider
import com.dynamix.core.cache.manager.CacheManagerProviderImpl
import com.dynamix.core.cache.provider.*
import com.dynamix.core.deviceutils.DynamixDeviceDetailProvider
import com.dynamix.core.deviceutils.DynamixDeviceDetailProviderImpl
import com.dynamix.core.deviceutils.DynamixDeviceHelper
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.logger.AppLoggerProviderImpl
import com.dynamix.core.navigation.NavigationProvider
import com.dynamix.core.navigation.NavigationProviderImpl
import com.dynamix.core.network.*
import com.dynamix.core.utils.Base64Provider
import com.dynamix.core.utils.Base64ProviderImpl
import com.dynamix.core.utils.JavaScriptInterfaceProvider
import com.dynamix.core.utils.JavaScriptInterfaceProviderImpl
import com.dynamix.core.view.terms_and_condition.DynamixTermsAndConditionVm
import com.google.gson.GsonBuilder
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun dynamixDeviceUtils(application: Application) = module {

    single {
        DynamixDeviceHelper(application, get())
    }

    single<DynamixDeviceDetailProvider> {
        DynamixDeviceDetailProviderImpl(get())
    }
}

fun dynamixCacheModule() = module {

    single<ApplicationDataCacheProvider> {
        ApplicationDataCacheProviderImpl()
    }

    single<IntermediateDataCacheProvider> {
        IntermediateDataCacheProviderImpl()
    }

    single<SessionCacheProvider> {
        SessionCacheProviderImpl()
    }

    single<PermanentCacheProvider> {
        PermanentCacheProviderImpl(get(), get())
    }

    single<PermanentGroupCacheProvider> {
        PermanentGroupCacheProviderImpl(get(), get())
    }

    single<CacheManagerProvider> {
        CacheManagerProviderImpl(get(), get(), get(), get(), get())
    }

    single {
        DynamixDataStorage()
    }
}

fun dynamixNetModule(
    application: Application,
    baseUrl: String,
    testApi: Boolean,
    debugMode: Boolean
) = module {

    single {
        GsonBuilder().create()
    }

    single {
        Dispatcher()
    }

    single {
        if (debugMode) {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        } else {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
        }
    }

    single {
        DynamixTokenInterceptor(application, get(), get(), get())
    }

    single {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .dispatcher(get())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<DynamixTokenInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get<OkHttpClient>())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single<ApiEndpoint> {
        if (testApi) {
            ApiEndPointTester()
        } else {
            get<Retrofit>().create(ApiEndpoint::class.java)
        }
    }

    single {
        ApiRouteProvider()
    }

    single<ApiProvider> {
        ApiProviderImpl(get(), get(), get(), get())
    }
}

fun dynamixCoreFramework() = module {

    single<AppLoggerProvider> {
        AppLoggerProviderImpl()
    }

    factory<NavigationProvider> { params ->
        NavigationProviderImpl(params[0], get())
    }

    single<Base64Provider> {
        Base64ProviderImpl()
    }

    single<JavaScriptInterfaceProvider> {
        JavaScriptInterfaceProviderImpl()
    }

    single {
        DynamixTermsAndConditionVm(get())
    }
}