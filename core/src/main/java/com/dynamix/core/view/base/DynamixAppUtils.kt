package com.dynamix.core.view.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dynamix.core.navigation.NavigationComponents
import com.dynamix.core.navigation.NavigationConstants
import com.dynamix.core.utils.DynamixConverter

object DynamixAppUtils {

    fun getFragment(
        context: Context,
        fragmentManager: FragmentManager,
        menuCode: String
    ): Fragment {
        return fragmentManager.fragmentFactory.instantiate(
            context.classLoader,
            NavigationComponents.getFragmentFromCode(menuCode)!!.name
        )
    }

    fun updateViewMargin(target: View, left: Int, top: Int, right: Int, bottom: Int) {
        var leftMargin = left
        var topMargin = top
        var rightMargin = right
        var bottomMargin = bottom
        val params = target.layoutParams as MarginLayoutParams
        leftMargin = if (leftMargin == -1) {
            params.leftMargin
        } else {
            DynamixConverter.dpToPx(target.context, leftMargin)
        }
        topMargin = if (topMargin == -1) {
            params.topMargin
        } else {
            DynamixConverter.dpToPx(target.context, topMargin)
        }
        rightMargin = if (rightMargin == -1) {
            params.rightMargin
        } else {
            DynamixConverter.dpToPx(target.context, rightMargin)
        }
        bottomMargin = if (bottomMargin == -1) {
            params.bottomMargin
        } else {
            DynamixConverter.dpToPx(target.context, bottomMargin)
        }
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
        target.layoutParams = params
    }

    fun handleAndSetEmptyText(textView: TextView, text: String?) {
        if (text == null || text.isEmpty()) {
            textView.text = "N/A"
        } else {
            textView.text = text
        }
    }

    fun hasDataBundle(intent: Intent): Boolean {
        return intent.hasExtra(NavigationConstants.NAV_DATA)
    }

    fun getDataBundle(intent: Intent): Bundle? {
        return intent.getBundleExtra(NavigationConstants.NAV_DATA)
    }
}