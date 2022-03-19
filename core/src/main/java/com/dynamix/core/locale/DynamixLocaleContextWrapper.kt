package com.dynamix.core.locale

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class DynamixLocaleContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var currentContext = context
            val resources = currentContext.resources
            val configuration = resources.configuration

            Locale.setDefault(newLocale)

            currentContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(newLocale);
                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                currentContext.createConfigurationContext(configuration)
            } else {
                configuration.setLocale(newLocale)
                currentContext.createConfigurationContext(configuration)
            }
            resources.updateConfiguration(configuration, resources.displayMetrics)

            return ContextWrapper(currentContext)
        }
    }
}