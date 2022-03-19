package com.dynamix.modsign.core.parser

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dynamix.modsign.core.FormHelper
import com.dynamix.modsign.core.parser.engine.BackgroundParser
import com.dynamix.modsign.model.RootView
import java.util.*

abstract class BaseParser protected constructor(
    protected var mContext: Context?,
    protected var mRootView: RootView
) {
    protected var mViews: List<View> = ArrayList()
    private var mLayoutParams: ViewGroup.LayoutParams? = null
    protected lateinit var mView: View

    private fun setupHeightWidthAndMargin() {
        mLayoutParams = if (mRootView.isRelative) {
            val layoutParams = RelativeLayout.LayoutParams(
                FormHelper.getDimension(mContext, mRootView.width),
                FormHelper.getDimension(mContext, mRootView.height)
            )
            setMargins(layoutParams)
            if (mRootView.isCenterInParent) {
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            if (mRootView.isCenterVertical) {
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
            }
            if (mRootView.isCenterHorizontal) {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            if (mRootView.isAlignParentBottom) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            if (mRootView.isAlignParentEnd) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
            layoutParams
        } else {
            val layoutParams = LinearLayout.LayoutParams(
                FormHelper.getDimension(mContext, mRootView.width),
                FormHelper.getDimension(mContext, mRootView.height)
            )
            setMargins(layoutParams)
            layoutParams
        }
    }

    protected fun setupLayout(view: View?) {
        view?.let {
            mView = view
            mView.id = ViewCompat.generateViewId()
            setupHeightWidthAndMargin()
            mView.tag = mRootView.id
            setPaddings()
            setVisibility()
            setBackground()
        }

    }

    protected fun setPaddings() {
        if (FormHelper.getDimension(mContext, mRootView.padding) > 0) {
            mView.setPadding(
                FormHelper.getDimension(mContext, mRootView.padding),
                FormHelper.getDimension(mContext, mRootView.padding),
                FormHelper.getDimension(mContext, mRootView.padding),
                FormHelper.getDimension(mContext, mRootView.padding)
            )
        } else {
            mView.setPadding(
                FormHelper.getDimension(mContext, mRootView.paddingLeft),
                FormHelper.getDimension(mContext, mRootView.paddingTop),
                FormHelper.getDimension(mContext, mRootView.paddingRight),
                FormHelper.getDimension(mContext, mRootView.paddingBottom)
            )
        }
    }

    protected fun setVisibility() {
        if (mRootView.isHidden) {
            mView.visibility = View.GONE
        }
    }

    protected fun setBackground() {
        if (mRootView.backgroundColor != null) {
            mView.setBackgroundColor(Color.parseColor(mRootView.backgroundColor))
        }
        if (mRootView.background != null) {
            mView.background =
                BackgroundParser.getInstance(mRootView.background!!).parse()
        }
        if (mRootView.backgroundImage != null) {
            Glide.with(mContext!!)
                .load(mRootView.backgroundImage)
                .into(object : CustomTarget<Drawable?>() {

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //clear
                    }
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        mView.background = resource
                    }
                })
        }
    }

    protected fun setMargins(layoutParams: ViewGroup.MarginLayoutParams) {
        if (FormHelper.getDimension(mContext, mRootView.margin) > 0) {
            layoutParams.setMargins(
                FormHelper.getDimension(mContext, mRootView.margin),
                FormHelper.getDimension(mContext, mRootView.margin),
                FormHelper.getDimension(mContext, mRootView.margin),
                FormHelper.getDimension(mContext, mRootView.margin)
            )
        } else {
            layoutParams.setMargins(
                FormHelper.getDimension(mContext, mRootView.marginLeft),
                FormHelper.getDimension(mContext, mRootView.marginTop),
                FormHelper.getDimension(mContext, mRootView.marginRight),
                FormHelper.getDimension(mContext, mRootView.marginBottom)
            )
        }
    }

    protected abstract fun parse(): BaseParser
    val layout: ParsedView
        get() {
            mView.layoutParams = mLayoutParams
            val parsedView = ParsedView(mView, mViews)
            parsedView.view = mView
            parsedView.viewList = mViews
            return parsedView
        }
}