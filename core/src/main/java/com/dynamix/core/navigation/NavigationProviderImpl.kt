package com.dynamix.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dynamix.core.event.DynamixActionConstants
import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.gate.DynamixGate
import com.dynamix.core.gate.DynamixGateController
import com.dynamix.core.logger.AppLoggerProvider
import java.io.Serializable

class NavigationProviderImpl(
    private val context: Context,
    private val appLoggerProvider: AppLoggerProvider
) : NavigationProvider {

    private var data = Bundle()
    private var mDataMap: Map<String, Any> = HashMap()

    override fun init(data: Bundle): NavigationProvider {
        this.data = data
        return this
    }

    override fun upToNoHistory(action: Class<out AppCompatActivity>?) {
        if (action == null) {
            appLoggerProvider.error("null action provided, aborting routing!")
            return
        }
        val intent = Intent(context, action)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
        if (!data.isEmpty) {
            intent.putExtra(NavigationConstants.NAV_DATA, data)
        }
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    override fun upTo(action: Class<out AppCompatActivity>?) {
        if (action == null) {
            appLoggerProvider.error("null action provided, aborting routing!")
            return
        }
        val intent = Intent(context, action)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (!data.isEmpty) {
            intent.putExtra(NavigationConstants.NAV_DATA, data)
        }
        if (mDataMap.isNotEmpty()) {
            intent.putExtra(NavigationConstants.DATA_MAP, mDataMap as Serializable)
        }
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    override fun upToWithResult(action: Class<out AppCompatActivity>?, requestCode: Int) {
        if (action == null) {
            appLoggerProvider.error("null action provided, aborting routing!")
            return
        }
        val intent = Intent(context, action)
        if (!data.isEmpty) {
            intent.putExtra(NavigationConstants.NAV_DATA, data)
        }
        (context as Activity).startActivityForResult(intent, requestCode)
        context.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    override fun upToClearTask(action: Class<out AppCompatActivity>?) {
        if (action == null) {
            appLoggerProvider.error("null action provided, aborting routing!")
            return
        }
        val intent = Intent(context, action)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        if (!data.isEmpty) {
            intent.putExtra(NavigationConstants.NAV_DATA, data)
        }
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        context.finishAffinity()
    }

    override fun navigate(navigator: Navigator, dataMap: Map<String, Any>) {
        mDataMap = dataMap;
        _navigate(navigator, dataMap)
    }

    override fun navigate(navigator: Navigator) {
        _navigate(navigator)
    }

    fun _navigate(navigator: Navigator, dataMap: Map<String, Any>? = null) {
        if (navigator.type == NavigationType.WEB_VIEW) {
            data.putString(NavigationConstants.PAGE_TITLE, navigator.name)
            data.putString(NavigationConstants.WEBVIEW_URL, navigator.navLink)
            upTo(NavigationComponents.getActivityFromCode(NavigationConstants.DYNAMIX_WEB_VIEW_ACTIVITY))
            return
        }
        if (navigator.event != null && navigator.event.action.equals(
                DynamixActionConstants.LOAD_FORM.type,
                ignoreCase = true
            )
        ) {
            data.putString(NavigationConstants.PAGE_TITLE, navigator.name)
            data.putParcelable(NavigationConstants.NAVIGATION_EVENT, navigator.event)

            if (navigator.requestCode != -1) {
                upToWithResult(
                    NavigationComponents.getActivityFromCode(navigator.code),
                    navigator.requestCode
                )
                return
            }
            upTo(NavigationComponents.getActivityFromCode(navigator.code))
            return
        }
        if (navigator.code.equals(NavigationType.MODSIGN, ignoreCase = true)) {
            val intentEvent = DynamixEvent(
                routeTitle = navigator.name,
                layoutUrl = navigator.navLink
            )
            data.putString(NavigationConstants.PAGE_TITLE, navigator.name)
            data.putParcelable(NavigationConstants.NAVIGATION_EVENT, intentEvent)

            if (navigator.requestCode != -1) {
                upToWithResult(
                    NavigationComponents.getActivityFromCode(navigator.code),
                    navigator.requestCode
                )
                return
            }
            upTo(NavigationComponents.getActivityFromCode(NavigationConstants.MODSIGN_ACTIVITY))
            return
        }


        if (NavigationComponents.getFragmentFromCode(navigator.code) != null) {
            data.putString(NavigationConstants.TITLE, navigator.name)
            data.putString(NavigationConstants.FRAGMENT_CODE, navigator.code)

//            navigate(
//                navigator.copy(
//                    code = NavigationConstants.DYNAMIX_CONTAINER_ACTIVITY
//                )
//            )
            navigate(
                navigator.copy(
                    code = NavigationConstants.CONTAINER_ACTIVITY
                )
            )
            return
        }
        if (NavigationComponents.getActivityFromCode(navigator.code) != null) {
            if (navigator.requestCode != -1) {
                upToWithResult(
                    NavigationComponents.getActivityFromCode(navigator.code),
                    navigator.requestCode
                )
                return
            }
            upTo(NavigationComponents.getActivityFromCode(navigator.code))
            return
        }
    }

    override fun navigate(event: DynamixEvent, data: Map<String, Any>) {
        event.routeCode?.let {

            if (event.menuType.isNotEmpty() && event.menuType.equals("WV", ignoreCase = true)) {
                navigate(
                    Navigator(
                        navLink = event.getRouteUrl()!!,
                        name = event.routeTitle,
                        type = event.menuType
                    ),
                    dataMap = data
                )
                return
            }
            if (event.gateType.isNotEmpty()) {
                handleGate(
                    DynamixGate(
                        type = event.gateType,
                        code = event.routeCode,
                        name = event.routeTitle,
                        data = data
                    )
                )
                return
            }
            if (event.routeCode.equals(NavigationType.MODSIGN)) {
                navigate(
                    Navigator(
                        code = event.routeCode,
                        navLink = event.getRouteUrl()!!,
                        name = event.routeTitle
                    ),
                    dataMap = data
                )
            } else {
                navigate(
                    Navigator(
                        code = event.routeCode,
                        name = event.routeTitle,
                        type = event.gateType
                    )
                )
            }
        }
    }

    private fun handleGate(gate: DynamixGate) {
        DynamixGateController.handleGate(context, gate)
    }
}