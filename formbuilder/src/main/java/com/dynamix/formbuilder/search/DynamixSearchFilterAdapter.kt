package com.dynamix.formbuilder.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SectionIndexer
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.databinding.DynamixRowFilterItemBinding
import java.util.*

class DynamixSearchFilterAdapter(
    private val dataList: List<String>,
    private val listener: FilterAdapterListener
) : RecyclerView.Adapter<DynamixSearchFilterAdapter.BindingHolder>(), Filterable, SectionIndexer {

    private var dataListFiltered: List<String>
    private val sectionPositions = mutableListOf<Int>()

    init {
        dataListFiltered = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val binding: DynamixRowFilterItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.dynamix_row_filter_item, parent, false
        )
        return BindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        val data = dataListFiltered[position]
        holder.binding.tvName.text = data
        holder.binding.clFilterItem.setOnClickListener {
            listener.onDataSelected(
                dataListFiltered[holder.adapterPosition]
            )
        }
    }

    override fun getItemCount(): Int {
        return dataListFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence?.toString() ?: ""
                dataListFiltered = if (charString.isEmpty()) {
                    dataList
                } else {
                    val filteredList: MutableList<String> = ArrayList()
                    for (row in dataList) {
                        if (row.lowercase().contains(charString.lowercase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = dataListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                if (filterResults.count > 0) {
                    dataListFiltered = filterResults.values as List<String>
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getSections(): Array<out Any> {
        val sections = mutableListOf<String>()
        sectionPositions.clear()
        var i = 0
        val size = dataList.size
        while (i < size) {
            val section = dataList[i][0].toString().uppercase()
            if (!sections.contains(section)) {
                sections.add(section)
                sectionPositions.add(i)
            }
            i++
        }
        return sections.toTypedArray()
    }

    override fun getPositionForSection(i: Int): Int {
        return sectionPositions[i]
    }

    override fun getSectionForPosition(i: Int): Int {
        return 0
    }

    interface FilterAdapterListener {
        fun onDataSelected(value: String)
    }

    inner class BindingHolder(var binding: DynamixRowFilterItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}