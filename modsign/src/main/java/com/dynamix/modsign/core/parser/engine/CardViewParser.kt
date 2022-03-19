package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.cardview.widget.CardView
import com.dynamix.modsign.core.FormHelper
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.core.parser.ParserFactory
import com.dynamix.modsign.model.RootView

class CardViewParser(context: Context, rootView: RootView) : BaseParser(context, rootView) {
    public override fun parse(): BaseParser {
        val cardView = CardView(mContext!!)
        if (!TextUtils.isEmpty(mRootView.cardElevation)) {
            cardView.cardElevation =
                FormHelper.getDimension(mContext, mRootView.cardElevation).toFloat()
        }
        if (!TextUtils.isEmpty(mRootView.cardRadius)) {
            cardView.radius = FormHelper.getDimension(mContext, mRootView.cardRadius).toFloat()
        }
        cardView.useCompatPadding = mRootView.isCardUseCompatPadding
        setupLayout(cardView)
        if (mRootView.children != null && !mRootView.children!!.isEmpty()) {
            for (children in mRootView.children!!) {
                val view: View = ParserFactory.getInstance(
                    mContext!!
                ).parse(children)
                cardView.addView(view)
            }
        }

        return this
    }
}