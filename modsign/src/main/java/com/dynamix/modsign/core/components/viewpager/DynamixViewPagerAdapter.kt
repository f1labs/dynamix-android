package com.dynamix.modsign.core.components.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dynamix.modsign.core.components.DynamixFragment
import com.dynamix.modsign.model.ViewPagerItem

class DynamixViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private var mViewPagerItems: List<ViewPagerItem>? = null
    private var mViewPagerFragments: ArrayList<DynamixFragment> = ArrayList()

    fun setItems(viewPagerItems: List<ViewPagerItem>?) {
        mViewPagerItems = viewPagerItems
    }

    fun getItem(pos: Int): DynamixFragment {
        return mViewPagerFragments[pos]
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = DynamixFragment.getInstance(mViewPagerItems!![position].layoutUrl)
        mViewPagerFragments.add(fragment)
        return mViewPagerFragments[position]
    }

    override fun getItemCount(): Int {
        return mViewPagerItems!!.size
    }
}