package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.text.TextUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.dynamix.modsign.ModSignController
import com.dynamix.R
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.model.RootView
import com.google.gson.internal.LinkedTreeMap
import java.util.*

class TextViewParser(val context: Context, rootView: RootView) : BaseParser(context, rootView) {
    public override fun parse(): BaseParser {
        val textView = TextView(context)
        setupLayout(textView)

        textView.setTag(R.id.imageUrl, mRootView.text)
        textView.text = mRootView.text

        setupStyles(mRootView, textView)
        setMaxLines(mRootView, textView)
        setEllipsize(mRootView, textView)
        setTextAlignment(mRootView, textView)

        textView.tag = mRootView.id

        return this
    }

    private fun setupStyles(rootView: RootView, textView: TextView) {
        if (rootView.style != null) {

            val builtStyle = buildStyle(rootView.style, ModSignController.instance.stylesMap)
            val hmIterator: Iterator<*> = builtStyle.entries.iterator()

            while (hmIterator.hasNext()) {
                val mapElement = hmIterator.next() as Map.Entry<*, *>
                when (mapElement.key as String?) {
                    "color" -> textView.setTextColor(Color.parseColor(mapElement.value as String?))
                    "textSize" -> textView.textSize = (mapElement.value as String?)!!.toFloat()
                    "font" -> try {
                        setFonts(context, mapElement.value as String, textView)
                    } catch (e: NotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun setFonts(context: Context, fontName: String, textView: TextView) {

        val typeface = when(fontName.lowercase(Locale.getDefault())) {
            "bold" -> ResourcesCompat.getFont(context, R.font.bold)
            "regular_bold" -> ResourcesCompat.getFont(context, R.font.regular)
            else -> ResourcesCompat.getFont(context, R.font.regular)
        }

        textView.typeface = typeface
    }

    private fun setMaxLines(rootView: RootView, textView: TextView) {
        if(rootView.maxLines > 0) {
            textView.maxLines = rootView.maxLines
        }
    }

    private fun setTextAlignment(rootView: RootView, textView: TextView) {
        rootView.textAlignment?.let {
            textView.textAlignment = when (rootView.textAlignment) {
                "center" ->  TextView.TEXT_ALIGNMENT_CENTER
                "right" -> TextView.TEXT_ALIGNMENT_VIEW_END
                else -> TextView.TEXT_ALIGNMENT_VIEW_START
            }
        }
    }

    private fun setEllipsize(rootView: RootView, textView: TextView) {
        rootView.ellipsize?.let {
            textView.ellipsize = TextUtils.TruncateAt.END
        }
    }

    private fun buildStyle(style: String, styles: Map<*, *>): Map<*, *> {
        val stylesMap = styles[style] as LinkedTreeMap<String, Any>

        if(stylesMap.containsKey("parent")) {
            val parentStyles = buildStyle(stylesMap["parent"] as String, styles)

            val hmIterator: Iterator<*> = parentStyles.entries.iterator()

            while(hmIterator.hasNext()) {
                val mapElement = hmIterator.next() as Map.Entry<*, *>
                if(!stylesMap.containsKey(mapElement.key)) {
                    stylesMap[mapElement.key as String] = mapElement.value
                }
            }

        }

        return stylesMap
    }
}