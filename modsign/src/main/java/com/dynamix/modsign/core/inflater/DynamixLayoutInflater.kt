package com.dynamix.modsign.core.inflater

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.dynamix.modsign.core.RootViewTypes
import com.dynamix.modsign.core.events.DynamixLayoutEvent
import com.dynamix.modsign.core.parser.engine.HorizontalContainerParser
import com.dynamix.modsign.core.parser.engine.VerticalContainerParser
import com.dynamix.modsign.model.LayoutWrapper
import com.dynamix.modsign.model.RootView

class DynamixLayoutInflater(val callback: Any) {

    lateinit var mRootView: RootView

    fun inflate(context: Context, layout: LayoutWrapper, parent: LinearLayout, data: Map<String, Any> = HashMap()): View {
        return viewInflater(context, layout, parent)
    }

    fun inflateAndPostInflate(context: Context, layout: LayoutWrapper, parent: LinearLayout, data: Map<String, Any> = HashMap()) {
        try {
            val renderedView = viewInflater(context, layout, parent)
            postInflater(context, renderedView, layout.layout!!, data)
        } catch (e: IllegalArgumentException) {

        }
    }

    private fun viewInflater(
        context: Context,
        layoutWrapper: LayoutWrapper,
        parent: LinearLayout
    ): View {
        val rootView = layoutWrapper.layout
        rootView.let {
            mRootView = rootView!!
        }

        return when {
            rootView?.type.equals(RootViewTypes.CONTAINER_VERTICAL, ignoreCase = true) -> {
                verticalViewParser(context, rootView!!, parent)
            }
            rootView?.type.equals(RootViewTypes.CONTAINER_HORIZONTAL, ignoreCase = true) -> {
                horizontalViewParser(context, rootView!!, parent)
            }
            else -> {
                throw IllegalArgumentException("View type ${rootView?.type} not supported")
            }
        }
    }

    private fun verticalViewParser(
        context: Context,
        rootView: RootView,
        parent: LinearLayout
    ): View {
        val parser: VerticalContainerParser =
            VerticalContainerParser.getInstance(
                context, rootView
            )
        parent.removeAllViews()
        val view = parser.parse().layout.view
        parent.addView(view)

        return view
    }

    private fun horizontalViewParser(
        context: Context,
        rootView: RootView,
        parent: LinearLayout
    ): View {
        val parser: HorizontalContainerParser =
            HorizontalContainerParser.Companion.getInstance(
                context, rootView
            )

        val view = parser.parse().layout.view

        parent.removeAllViews()
        parent.addView(view)

        return view
    }

    private fun postInflater(context: Context, view: View, rootView: RootView, data: Map<String, Any> = HashMap()) {
        val postInflateViewParser =
            PostInflateViewParser(context as FragmentActivity, view, callback)
        val viewData = postInflateViewParser.viewInflated(view, rootView, data)
        (callback as DynamixLayoutEvent).onViewInflated(viewData)
    }

}