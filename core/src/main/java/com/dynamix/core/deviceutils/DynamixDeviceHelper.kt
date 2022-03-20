package com.dynamix.core.deviceutils

import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.dynamix.core.logger.AppLoggerProvider
import io.reactivex.Observable
import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * Created by shreejan.shrestha on 8/25/2017.
 */
class DynamixDeviceHelper(
    private val application: Application,
    private val loggerProvider: AppLoggerProvider
) {

    @SuppressLint("HardwareIds")
    fun getDeviceId(): Observable<String> {
        if (Build.VERSION.SDK_INT > 28) {
            val androidId =
                Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            return Observable.just(androidId)
        }
        val telephonyManager =
            application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val deviceId1 = telephonyManager.deviceId
            if (deviceId1 == null || deviceId1.matches("[0]+".toRegex())) {
                val androidId = Settings.Secure.getString(
                    application.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) ?: throw SecurityException()
                return Observable.just(androidId)
            }
            val deviceId2 = StringBuilder(deviceId1).reverse().toString()
            val deviceId = UUID(
                deviceId1.hashCode().toLong(), deviceId2.hashCode().toLong()
            ).toString()
            loggerProvider.debug("Device id is :::$deviceId")
            return Observable.just(deviceId)
        }
        throw SecurityException()
    }

    @get:SuppressLint("HardwareIds")
    val deviceIdString: String
        get() {
            if (Build.VERSION.SDK_INT > 28) {
                return Settings.Secure.getString(
                    application.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
            val telephonyManager =
                application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val deviceId1 = telephonyManager.deviceId
                if (deviceId1 == null || deviceId1.matches("[0]+".toRegex())) {
                    return Settings.Secure.getString(
                        application.contentResolver,
                        Settings.Secure.ANDROID_ID
                    ) ?: throw SecurityException()
                }
                val deviceId2 = StringBuilder(deviceId1).reverse().toString()
                val deviceId = UUID(
                    deviceId1.hashCode().toLong(), deviceId2.hashCode().toLong()
                ).toString()
                loggerProvider.debug("Device id is :::$deviceId")
                return deviceId
            }
            return ""
        }

    // API level 8+
    @get:SuppressLint("MissingPermission")
    val emailAddress: Observable<String>
        get() {
            val emailPattern = Patterns.EMAIL_ADDRESS // API level 8+
            val accounts = AccountManager.get(application).getAccountsByType("com.google")
            for (account in accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    return Observable.just(account.name)
                }
            }
            return Observable.just("")
        }
    val deviceModel: Observable<String>
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize("$manufacturer $model")
            }
        }

    private fun capitalize(s: String?): Observable<String> {
        if (s == null || s.length == 0) {
            return Observable.just("")
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            Observable.just(s)
        } else {
            Observable.just(Character.toUpperCase(first).toString() + s.substring(1))
        }
    }

    val screenWidthInPixels: Observable<Int>
        get() {
            val dm = DisplayMetrics()
            val windowManager =
                application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(dm)
            return Observable.just(dm.widthPixels)
        }
    val screenHeightInPixels: Observable<Int>
        get() {
            val dm = DisplayMetrics()
            val windowManager =
                application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(dm)
            return Observable.just(dm.heightPixels)
        }

    fun getDeviceDetails(): Observable<String> {
        val deviceInfo = JSONObject()
        val deviceDetailObs = listOf(
            getPhoneNumber(),
            getEmail(),
            getIMEI(),
            getManufacturer(),
            getModel(),
            getAndroidVersion(),
            getSimSerialNumber(),
            getCarrier(),
            getLatitude(),
            getLongitude(),
            getSDKVersion(),
            getAppVersion()
        )
        return Observable.zip(deviceDetailObs) { objects: Array<Any> ->
            deviceInfo.put("phoneNumber", objects[0].toString())
            deviceInfo.put("email", objects[1].toString())
            deviceInfo.put("imei", objects[2].toString())
            deviceInfo.put("manufacturer", objects[3].toString())
            deviceInfo.put("model", objects[4].toString())
            deviceInfo.put("os", objects[5].toString())
            deviceInfo.put("simSerialNumber", objects[6].toString())
            deviceInfo.put("carrier", objects[7].toString())
            deviceInfo.put("lat", objects[8].toString())
            deviceInfo.put("long", objects[9].toString())
            deviceInfo.put("deviceRooted", isRooted())
            deviceInfo.put("sdkVersion", objects[10].toString())
            deviceInfo.put("appVersion", objects[11].toString())
            deviceInfo.toString()
        }
    }

    private fun getEmail(): Observable<String> =
        Observable.just("N/A")

    @SuppressLint("HardwareIds")
    private fun getPhoneNumber(): Observable<String> = try {
        val tMgr = application.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        var mPhoneNumber: String? = null
        if (tMgr != null) {
            mPhoneNumber = tMgr.line1Number
        }
        if (mPhoneNumber != null) {
            Observable.just(mPhoneNumber)
        } else {
            Observable.just("N/A")
        }
    } catch (e: SecurityException) {
        Observable.just("N/A")
    }

    fun getModel(): Observable<String> = Observable.just(Build.MODEL)

    @SuppressLint("HardwareIds")
    fun getSimSerialNumber(): Observable<String> = Observable.just("N/A")

    private fun getCarrier(): Observable<String> {
        val telephonyManager =
            application.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return if (telephonyManager != null) {
            Observable.just(telephonyManager.simOperatorName)
        } else Observable.just("N/A")
    }

    @SuppressLint("HardwareIds")
    private fun getIMEI(): Observable<String> {
        return Observable.just("N/A")
    }

    private fun getSDKVersion(): Observable<String> {
        return Observable.just(Build.VERSION.SDK_INT.toString())
    }

    private fun getManufacturer(): Observable<String> {
        return Observable.just(Build.MANUFACTURER)
    }

    private fun getAndroidVersion(): Observable<String> {
        return Observable.just(Build.VERSION.RELEASE)
    }

    private fun getAppVersion(): Observable<String> {
        return try {
            val manager = application.packageManager
            val info = manager.getPackageInfo(
                application.packageName, 0
            )
            Observable.just(info.versionName)
        } catch (e: Exception) {
            loggerProvider.error(e.toString())
            Observable.just("N/A")
        }
    }

    private fun getLongitude(): Observable<String> {
        val lm = application.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        try {
            var location: Location? = null
            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (location != null) {
                return Observable.just(java.lang.Double.toString(location.longitude))
            }
        } catch (e: SecurityException) {
            return Observable.just("N/A")
        }
        return Observable.just("N/A")
    }

    private fun getLatitude(): Observable<String> {
        val lm = application.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        try {
            var location: Location? = null
            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (location != null) {
                return Observable.just(java.lang.Double.toString(location.latitude))
            }
        } catch (e: SecurityException) {
            return Observable.just("N/A")
        }
        return Observable.just("N/A")
    }

    /**
     * Checks if the device is rooted.
     *
     * @return `true` if the device is rooted, `false` otherwise.
     */
    private fun isRooted(): Boolean {

        // get from build info
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("tokenRetrieved-keys")) {
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
        val executedSuccesfully: Boolean = try {
            Runtime.getRuntime().exec(command)
            true
        } catch (e: Exception) {
            false
        }
        return executedSuccesfully
    }
}