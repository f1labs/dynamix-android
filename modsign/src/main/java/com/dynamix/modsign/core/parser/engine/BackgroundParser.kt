package com.dynamix.modsign.core.parser.engine

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.dynamix.modsign.model.Background

class BackgroundParser private constructor(private val mBackground: Background) {
    fun parse(): Drawable {
        val drawable = GradientDrawable()
        if (mBackground.width > 0 && mBackground.height > 0) {
            drawable.setSize(mBackground.width, mBackground.height)
        }
        setDrawableType(drawable)
        drawable.shape = getShape(mBackground.shape)
        if (mBackground.cornerRadius > 0) {
            drawable.cornerRadius = mBackground.cornerRadius
        }
        if (mBackground.borderWidth > 0 && mBackground.borderColor != null) {
            drawable.setStroke(
                mBackground.borderWidth, Color.parseColor(
                    mBackground.borderColor
                )
            )
        }
        return drawable
    }

    private fun getShape(shape: String?): Int {
        return when (shape!!.toLowerCase()) {
            OVAL -> GradientDrawable.OVAL
            LINE -> GradientDrawable.LINE
            RING -> GradientDrawable.RING
            else -> GradientDrawable.RECTANGLE
        }
    }

    private fun setDrawableType(drawable: GradientDrawable) {
        var type: String? = SOLID
        if (mBackground.type != null) {
            type = mBackground.type
        }
        if (type.equals(GRADIENT, ignoreCase = true)) {
            drawable.orientation = orientation
            if (mBackground.centerColor != null) {
                drawable.colors = intArrayOf(
                    Color.parseColor(mBackground.startColor),
                    Color.parseColor(mBackground.centerColor),
                    Color.parseColor(mBackground.endColor)
                )
            } else {
                drawable.colors = intArrayOf(
                    Color.parseColor(mBackground.startColor),
                    Color.parseColor(mBackground.endColor)
                )
            }
        } else {
            drawable.setColor(Color.parseColor(mBackground.backgroundColor))
        }
    }

    private val orientation: GradientDrawable.Orientation
        get() = when (mBackground.angle) {
            45 -> GradientDrawable.Orientation.BL_TR
            90 -> GradientDrawable.Orientation.BOTTOM_TOP
            135 -> GradientDrawable.Orientation.BR_TL
            180 -> GradientDrawable.Orientation.RIGHT_LEFT
            225 -> GradientDrawable.Orientation.TR_BL
            270 -> GradientDrawable.Orientation.TOP_BOTTOM
            315 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.LEFT_RIGHT
        }

    companion object {
        private const val GRADIENT = "gradient"
        private const val SOLID = "solid"
        private const val RECTANGLE = "rectangle"
        private const val OVAL = "oval"
        private const val LINE = "line"
        private const val RING = "ring"
        fun getInstance(background: Background): BackgroundParser {
            return BackgroundParser(background)
        }
    }
}