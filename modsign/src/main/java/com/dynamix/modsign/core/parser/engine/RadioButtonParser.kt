package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.content.res.ColorStateList
import android.widget.CheckBox
import android.widget.RadioButton
import com.dynamix.R
import com.dynamix.core.utils.DynamixResourceUtils
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.model.RootView

class RadioButtonParser(val context: Context, rootView: RootView) : BaseParser(context, rootView) {

    public override fun parse(): BaseParser {
        val radioButton = RadioButton(context)
        setupLayout(radioButton)

        radioButton.buttonTintList = ColorStateList.valueOf(
            DynamixResourceUtils.getColorFromThemeAttributes(
                context,
                R.attr.colorPrimary
            )
        )
        radioButton.tag = mRootView.id
        return this
    }
}