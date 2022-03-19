package com.dynamix.modsign.core.parser.engine

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.dynamix.modsign.core.components.viewpager.DynamixViewPagerAdapter
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.model.RootView

class ViewPagerParser(context: Context, rootView: RootView) : BaseParser(context, rootView) {

    public override fun parse(): BaseParser {
        val viewPager2 = ViewPager2(mContext!!)
        setupLayout(viewPager2)
        return this
    }

    companion object {
        fun postInflate(context: Context, viewPager2: ViewPager2, view: RootView, viewData: HashMap<String, Any>) {
            val adapter = DynamixViewPagerAdapter(context as FragmentActivity)
            adapter.setItems(view.viewPagerItems)
            viewData[view.id!!] = adapter
            viewPager2.adapter = adapter
        }
    }
}