package com.dynamix.core.network

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.dynamix.core.DynamixConstants
import com.dynamix.core.deviceutils.DynamixDeviceDetailProvider
import com.dynamix.core.deviceutils.DynamixDeviceDetails
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.logger.AppLoggerProvider
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class DynamixTokenInterceptor(
    private val application: Application,
    private val routeProvider: ApiRouteProvider,
    private val loggerProvider: AppLoggerProvider,
    private val deviceDetailProvider: DynamixDeviceDetailProvider
) : Interceptor {

    private fun isUrlPublic(url: String): Boolean {
        var isUrlPublic = false
        for ((key) in routeProvider.publicRoutes()) {
            if (routeProvider.getRoute(key).url.equals(url, ignoreCase = true)) {
                isUrlPublic = true
                break
            }
        }
        return isUrlPublic
    }

    private fun isUrlIgnored(url: String): Boolean {
        var isUrlIgnored = false
        for ((key) in routeProvider.ignoredRoutes()) {
            if (routeProvider.getRoute(key).url.equals(url, ignoreCase = true)) {
                isUrlIgnored = true
                break
            }
        }
        return isUrlIgnored
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var requestBody = request.body

        if (!isUrlIgnored(request.url.toString())) {
            if (requestBody != null) {
                val subtype = requestBody.contentType()!!.subtype
                if (subtype.contains(DynamixConstants.JSON)) {
                    requestBody = processApplicationJsonRequestBody(request)
                }
                if (requestBody != null) {
                    request = if (!isUrlPublic(request.url.toString())) {
                        if (DynamixEnvironmentData.token.isNotEmpty()
                        ) {
                            addHeaders(request, DynamixEnvironmentData.token, requestBody)
                        } else {
                            addHeaders(request, requestBody = requestBody)
                        }
                    } else {
                        addHeaders(request, requestBody = requestBody)
                    }
                } else {
                    if (!isUrlPublic(request.url.toString())) {
                        if (DynamixEnvironmentData.token.isNotEmpty()) {
                            request = addHeaders(request, DynamixEnvironmentData.token)
                        }
                    }
                }
            } else {
                if (!isUrlPublic(request.url.toString())) {
                    if (DynamixEnvironmentData.token.isNotEmpty()) {
                        request = addHeaders(request, DynamixEnvironmentData.token)
                    }
                }
            }
        }
        return chain.proceed(request)
    }

    private fun addHeaders(
        request: Request,
        token: String? = null,
        requestBody: RequestBody? = null
    ): Request {
        val requestBuilder = request.newBuilder()
        if (token != null) {
            requestBuilder
                .addHeader("Authorization", "Bearer " + DynamixEnvironmentData.token)
        }
        if (!DynamixEnvironmentData.requestHeadersMap.isNullOrEmpty()) {
            DynamixEnvironmentData.requestHeadersMap.forEach {
                requestBuilder.addHeader(it.key, it.value)
            }
        }
        if (requestBody != null) {
            requestBuilder.post(requestBody)
        }
        return requestBuilder.build()
    }

    private fun processApplicationJsonRequestBody(request: Request): RequestBody? {
        val customReq = bodyToString(request.body)
        try {
            val obj = JSONObject(customReq)
            if (!isUrlPublic(request.url.toString())) {
                if (DynamixEnvironmentData.token.isNotEmpty()) {
                    obj.put(DynamixConstants.TOKEN, DynamixEnvironmentData.token)
                }
            }
            obj.put("channel", "mobile")
            obj.put(DynamixConstants.DEVICE_DETAILS, getDeviceDetails())
            obj.put(DynamixConstants.DEVICE_ID, deviceDetailProvider.deviceIdAsString)

            return obj.toString().toRequestBody(request.body!!.contentType())
        } catch (e: JSONException) {
            loggerProvider.error(e)
        }
        return null
    }

    private fun bodyToString(requestBody: RequestBody?): String {
        try {
            Buffer().use { buffer ->
                if (requestBody != null) {
                    if (requestBody.contentLength() > 0) {
                        requestBody.writeTo(buffer)
                    } else {
                        return "{}"
                    }
                } else return ""
                return buffer.readUtf8()
            }
        } catch (e: IOException) {
            loggerProvider.info(e.localizedMessage)
            return "did not work"
        }
    }

    /**
     * Checks if the device is rooted.
     *
     * @return `true` if the device is rooted, `false` otherwise.
     */
    private fun isRooted(): Boolean {
        // get from build info
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }
        // check if /system/app/Superuser.apk is present
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
        } catch (e1: Exception) {
            // ignore
        }
        // try executing commands
        return (canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su"))
    }

    // executes a command on the system
    private fun canExecuteCommand(command: String): Boolean {
        val executedSuccessfully: Boolean = try {
            Runtime.getRuntime().exec(command)
            true
        } catch (e: Exception) {
            false
        }
        return executedSuccessfully
    }

    private fun developerOptionsEnabled(): Boolean {
        return Settings.System.getInt(
            application.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) == 1
    }

    private fun dontKeepActivitiesEnabled(): Boolean {
        return Settings.System.getInt(
            application.contentResolver,
            Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0
        ) == 1
    }

    fun getDeviceDetails(): String {
        var deviceDetails = DynamixDeviceDetails(
            isDeviceRooted = isRooted(),
            isDeveloperOptionsEnabled = developerOptionsEnabled(),
            isDontKeepActivitiesEnabled = dontKeepActivitiesEnabled(),
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            sdkVersion = Build.VERSION.SDK_INT,
            androidVersion = Build.VERSION.RELEASE,
        )
        try {
            val pInfo = application.packageManager.getPackageInfo(
                application.packageName,
                0
            );
            deviceDetails = deviceDetails.copy(
                appVersionName = pInfo.versionName,
                appVersionCode = pInfo.versionCode.toString(),
            )
        } catch (e: PackageManager.NameNotFoundException) {
            loggerProvider.error(e)
            deviceDetails = deviceDetails.copy(
                appVersionName = "versionName",
                appVersionCode = "versionCode",
            )
        }
        val deviceDetailsString = Gson().toJson(deviceDetails)
        loggerProvider.info("Device Info::: $deviceDetailsString")
        return deviceDetailsString
    }
}