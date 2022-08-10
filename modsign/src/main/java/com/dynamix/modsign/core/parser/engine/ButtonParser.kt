package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import com.dynamix.R
import com.dynamix.modsign.core.events.DynamixButtonEvent
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.model.RootView
import com.google.android.material.button.MaterialButton
import java.util.*

class ButtonParser(val context: Context, rootView: RootView) : BaseParser(context, rootView) {
    public override fun parse(): BaseParser {
        val button =
            MaterialButton(ContextThemeWrapper(context, R.style.Widget_MaterialComponents_Button))
        setupLayout(button)

        button.text = mRootView.getText()
        //setupStyles(mRootView, button)
        //setTextAlignment(mRootView, button)
        button.tag = mRootView.id

        return this
    }

//    private fun setupStyles(rootView: RootView, textView: TextView) {
//        if (rootView.style != null) {
//
//            val builtStyle = buildStyle(rootView.style, ModSignController.instance.stylesMap)
//            val hmIterator: Iterator<*> = builtStyle.entries.iterator()
//
//            while (hmIterator.hasNext()) {
//                val mapElement = hmIterator.next() as Map.Entry<*, *>
//                when (mapElement.key as String?) {
//                    "color" -> textView.setTextColor(Color.parseColor(mapElement.value as String?))
//                    "textSize" -> textView.textSize = (mapElement.value as String?)!!.toFloat()
//                    "font" -> try {
//                        setFonts(context, mapElement.value as String, textView)
//                    } catch (e: Resources.NotFoundException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
//    }

    private fun setFonts(context: Context, fontName: String, textView: TextView) {

        val typeface = when (fontName.lowercase(Locale.getDefault())) {
            "bold" -> ResourcesCompat.getFont(context, R.font.bold)
            "regular_bold" -> ResourcesCompat.getFont(context, R.font.regular)
            else -> ResourcesCompat.getFont(context, R.font.regular)
        }

        textView.typeface = typeface
    }

    private fun setTextAlignment(rootView: RootView, button: MaterialButton) {
        rootView.textAlignment?.let {
            button.textAlignment = when (rootView.textAlignment) {
                "center" -> View.TEXT_ALIGNMENT_CENTER
                "right" -> View.TEXT_ALIGNMENT_VIEW_END
                else -> View.TEXT_ALIGNMENT_VIEW_START
            }
        }
    }

    companion object {

        fun postInflate(
            callback: Any,
            button: MaterialButton,
            view: RootView,
        ) {
            button.setOnClickListener {
                (callback as DynamixButtonEvent).onButtonClicked(view)
            }
        }
    }
}