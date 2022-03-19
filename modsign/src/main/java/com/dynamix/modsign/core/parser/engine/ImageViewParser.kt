package com.dynamix.modsign.core.parser.engine

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dynamix.R
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.model.RootView

class ImageViewParser(context: Context, rootView: RootView) : BaseParser(context, rootView) {
    public override fun parse(): BaseParser {
        val imageView = ImageView(mContext)
        imageView.setTag(R.id.imageUrl, mRootView.getImageUrl())
        imageView.adjustViewBounds = true
        setupLayout(imageView)
        if (!TextUtils.isEmpty(mRootView.getImageUrl()) && mRootView.getImageUrl()!!.startsWith("http")) {
            Glide.with(imageView.context)
                .load(mRootView.getImageUrl())
                .into(object : CustomTarget<Drawable?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        //clear if needed
                    }
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        imageView.setImageDrawable(resource)
                    }
                })
        }

        return this
    }
}