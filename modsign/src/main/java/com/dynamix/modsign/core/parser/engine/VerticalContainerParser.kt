package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.core.parser.ParserFactory
import com.dynamix.modsign.model.RootView

class VerticalContainerParser protected constructor(context: Context, rootView: RootView) :
    BaseParser(context, rootView) {

    public override fun parse(): BaseParser {
        val verticalContainer = mRootView
        if (mRootView.isRelative) {
            relativeVerticalLayoutParser(verticalContainer)
        } else {
            linearVerticalLayoutParser(verticalContainer)
        }
        return this
    }

    private fun linearVerticalLayoutParser(verticalContainer: RootView) {
        val linearLayout = LinearLayout(mContext)
        setupLayout(linearLayout)
        linearLayout.orientation = LinearLayout.VERTICAL
        if (verticalContainer.children != null && verticalContainer.children.isNotEmpty()) {
            for (children in verticalContainer.children) {
                val view: View = ParserFactory.getInstance(
                    mContext!!
                ).parse(children)
                linearLayout.addView(view)
            }
        }
        setupEqualHeightLayout(verticalContainer, linearLayout)
    }

    private fun setupEqualHeightLayout(verticalContainer: RootView, linearLayout: LinearLayout) {
        if (verticalContainer.isEqualWidth && linearLayout.childCount > 0) {
            for (position in 0 until linearLayout.childCount) {
                val view = linearLayout.getChildAt(position)
                (view.layoutParams as LinearLayout.LayoutParams).weight = 1f
            }
        }
    }

    private fun relativeVerticalLayoutParser(verticalContainer: RootView) {
        val relativeLayout = RelativeLayout(mContext)
        setupLayout(relativeLayout)
        if (verticalContainer.children != null && verticalContainer.children.isNotEmpty()) {
            for (children in verticalContainer.children) {
                val view: View = ParserFactory.getInstance(
                    mContext!!
                ).parse(children)
                relativeLayout.addView(view)
            }
        }
    }

    companion object {
        fun getInstance(context: Context, rootView: RootView): VerticalContainerParser {
            return VerticalContainerParser(context, rootView)
        }
    }
}