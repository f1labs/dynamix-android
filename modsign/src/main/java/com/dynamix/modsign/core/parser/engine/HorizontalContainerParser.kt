package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.core.parser.ParserFactory
import com.dynamix.modsign.model.RootView

class HorizontalContainerParser private constructor(context: Context, rootView: RootView) :
    BaseParser(context, rootView) {
    public override fun parse(): BaseParser {
        val horizontalContainer = mRootView
        if (mRootView.isRelative) {
            parseRelativeHorizontalLayout(horizontalContainer)
        } else {
            parseLinearHorizontalLayout(horizontalContainer)
        }
        return this
    }

    private fun parseLinearHorizontalLayout(horizontalContainer: RootView) {
        val layout = LinearLayout(mContext)
        setupLayout(layout)
        layout.orientation = LinearLayout.HORIZONTAL
        if (horizontalContainer.children != null && horizontalContainer.children.isNotEmpty()) {
            for (children in horizontalContainer.children) {
                layout.addView(
                    ParserFactory.getInstance(
                        mContext!!
                    ).parse(children)
                )
            }
        }

        layout.tag = mRootView.id
        setupEqualWidthLayout(horizontalContainer, layout)
    }

    private fun parseRelativeHorizontalLayout(horizontalContainer: RootView): View {
        val layout = RelativeLayout(mContext)
        setupLayout(layout)
        if (horizontalContainer.children != null && horizontalContainer.children.isNotEmpty()) {
            for (children in horizontalContainer.children) {
                layout.addView(
                    ParserFactory.getInstance(
                        mContext!!
                    ).parse(children)
                )
            }
        }
        layout.tag = mRootView.id
        return layout
    }

    private fun setupEqualWidthLayout(horizontalContainer: RootView, layout: LinearLayout) {
        if (horizontalContainer.isEqualWidth && layout.childCount > 0) {
            for (position in 0 until layout.childCount) {
                val view = layout.getChildAt(position)
                (view.layoutParams as LinearLayout.LayoutParams).weight = 1f
            }
        }
    }

    companion object {
        fun getInstance(context: Context, rootView: RootView): HorizontalContainerParser {
            return HorizontalContainerParser(context, rootView)
        }
    }
}