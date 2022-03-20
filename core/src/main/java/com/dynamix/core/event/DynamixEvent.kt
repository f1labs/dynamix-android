package com.dynamix.core.event

import android.os.Parcelable
import com.dynamix.core.init.AppInitializer
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.regex.Pattern

@Parcelize
data class DynamixEvent(
    val action: String = "",
    val message: String? = null,
    val data: String? = null,
    val event: DynamixEvent? = null,
    val api: String? = null,
    @SerializedName("routeUrl")
    private var _routeUrl: String? = null,
    val routeTitle: String = "",
    val routeCode: String? = null,
    val layoutCode: String? = "",
    val storeDataKey: String = "",
    val storageId: String = "",
    val layoutUrl: String = "",
    val code: String? = "",
    val name: String? = "",
    val gateType: String = "",
    val requestCode: Int = -1,
    var menuType: String = "",
    var navLink: String = "",
) : Parcelable {

    fun setRouteUrl(routeUrl: String) {
        _routeUrl = routeUrl
    }

    fun getRouteUrl(): String? {
        if (_routeUrl == null) {
            return null
        }
        _routeUrl = getDataUrl(_routeUrl!!)

        return _routeUrl
    }

    private fun getDataUrl(url: String): String {
        var finalUrl = url

        val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
        val matcher = pattern.matcher(url)
        while (matcher.find()) {
            if(AppInitializer.urlMap.containsKey(matcher.group(1))) {
                finalUrl = finalUrl.replace(
                    "{{" + matcher.group(1) + "}}",
                    AppInitializer.urlMap[matcher.group(1)] as String,
                    true
                )
            }
        }
        return finalUrl
    }
}

object DynamixEventAction {
    const val ROUTE = "route"
    const val WEB_VIEW = "webView"
}