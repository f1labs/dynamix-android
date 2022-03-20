package com.dynamix.modsign.core.components.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dynamix.R
import com.dynamix.modsign.core.inflater.DynamixLayoutInflater
import com.dynamix.modsign.core.inflater.PostInflateViewParser
import com.dynamix.modsign.model.LayoutWrapper
import com.dynamix.modsign.model.RootView
import com.google.gson.Gson
import kotlin.math.max

class RvAdapter(
    private val mContext: Context,
    private val mDataList: List<Map<String, Any>>,
    private val mLayout: String,
    private val mCallback: Any
) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {


    private var checkboxId: String? = null
    private var maxItems: Int = 0
    var lastSelectedPosition = -1
    lateinit var mRootView: RootView

    fun setSingleItemSelectionId(id: String) {
        checkboxId = id
    }

    fun setMaxItems(items: Int) {
        maxItems = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.row_rv, parent, false)
        val wrapper = view.findViewById<LinearLayout>(R.id.wrapper)

        val gson = Gson()
        val layoutWrapper = gson.fromJson(mLayout, LayoutWrapper::class.java)
        mRootView = layoutWrapper.layout!!

        return ViewHolder(wrapper, mLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postInflateViewParser =
            PostInflateViewParser(mContext as FragmentActivity, holder.itemView, mCallback)
        postInflateViewParser.viewInflated(holder.itemView, mRootView, mDataList[position])
        (mCallback as DynamixRvAdapterEvent).onBindViewHolder(holder.itemView, mDataList[position])

        if (checkboxId != null) {

            val checkBox = holder.itemView.findViewWithTag<RadioButton>(checkboxId)
            checkBox.isChecked = position == lastSelectedPosition

            checkBox.setOnClickListener {
                if (lastSelectedPosition >= 0)
                    notifyItemChanged(lastSelectedPosition)
                lastSelectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(lastSelectedPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return if(maxItems == 0 || mDataList.size < maxItems) {
            mDataList.size
        } else {
            maxItems
        }
    }

    fun getSelectedItem(): Map<String, Any> {
        return mDataList[lastSelectedPosition]
    }

    public class ViewHolder(itemView: View, layout: String) : RecyclerView.ViewHolder(itemView) {
        companion object {
            lateinit var mCallback: Any
            lateinit var mContext: Context
        }

        init {
            val gson = Gson()
            val layoutWrapper = gson.fromJson(layout, LayoutWrapper::class.java)

            val layoutInflater = DynamixLayoutInflater(mCallback)
            layoutInflater.inflate(mContext, layoutWrapper, itemView as LinearLayout)
        }
    }

    init {
        ViewHolder.mCallback = mCallback
        ViewHolder.mContext = mContext
    }
}

interface DynamixRvAdapterEvent {
    fun onBindViewHolder(itemView: View, data: Any)
}