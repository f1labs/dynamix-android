package com.dynamix.core.utils.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Created by Kiran Gyawali on 11/11/2018.
 */
object PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean {
        return (ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun hasCameraPermission(context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun hasCameraAndPhoneStatePermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasExternalStorageWritePermission(context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    fun requestExternalStoragePermission(activity: Activity) {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(
            activity, permissions,
            PermissionConstants.REQ_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    fun hasReadPhoneStatePermission(context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun hasSendSmsPermission(context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun hasPermissionForProfileImage(context: Context): Boolean {
        return hasExternalStorageWritePermission(context) && hasCameraPermission(context)
    }

    @JvmStatic
    fun requestPermissionsForProfileImage(activity: Activity) {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        ActivityCompat.requestPermissions(
            activity, permissions,
            PermissionConstants.REQ_CODE_PROFILE_IMAGE
        )
    }

    fun requestPermissionForPhoneState(activity: Activity, requestCode: Int) {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun hasPermissionForLocationAccess(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissionForChooseImage(context: Context): Boolean {
        return hasExternalStorageWritePermission(context) && hasCameraPermission(context)
    }

    fun requestLocationPermissionForMaps(activity: Activity) {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            PermissionConstants.REQ_CODE_ACCESS_FINE_LOCATION
        )
    }

    @JvmStatic
    fun hasCallPhonePermission(context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun requestPermissionForPhoneCall(activity: Activity, requestCode: Int) {
        val permissions = arrayOf(Manifest.permission.CALL_PHONE)
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun requestCameraPermission(activity: Activity) {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            PermissionConstants.REQ_CODE_CAMERA
        )
    }

    /**
     * Contacts permissions
     */
    fun hasReadContactsPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
}