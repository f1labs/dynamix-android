package com.dynamix.modsign.core.parser

import android.content.Context
import android.view.View
import com.dynamix.modsign.core.RootViewTypes
import com.dynamix.modsign.core.parser.engine.*
import com.dynamix.modsign.model.RootView

class ParserFactory private constructor(private val mContext: Context) : Parser {
    override fun parse(rootView: RootView): View {
        return when (rootView.type) {
            RootViewTypes.CONTAINER_HORIZONTAL -> HorizontalContainerParser.Companion.getInstance(
                mContext,
                rootView
            ).parse().layout.view
            RootViewTypes.CONTAINER_VERTICAL -> VerticalContainerParser.Companion.getInstance(
                mContext,
                rootView
            ).parse().layout.view
            RootViewTypes.TEXTVIEW -> {
                val parser = TextViewParser(mContext, rootView)
                parser.parse().layout.view
            }
            RootViewTypes.CHECKBOX -> CheckboxParser(mContext, rootView).parse().layout.view
            RootViewTypes.RADIO -> RadioButtonParser(mContext, rootView).parse().layout.view
            RootViewTypes.IMAGE -> ImageViewParser(mContext, rootView).parse().layout.view
            RootViewTypes.CARD_VIEW -> CardViewParser(mContext, rootView).parse().layout.view
            RootViewTypes.RECYCLER_VIEW -> RecyclerViewParser(
                mContext,
                rootView
            ).parse().layout.view
            RootViewTypes.VIEWPAGER -> ViewPagerParser(mContext, rootView).parse().layout.view
            RootViewTypes.BUTTON -> ButtonParser(mContext, rootView).parse().layout.view
            else -> throw IllegalArgumentException("View "+rootView.type+" type not defined")
        }
    }

    companion object {
        fun getInstance(context: Context): ParserFactory {
            return ParserFactory(context)
        }
    }
}