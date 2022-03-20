package com.dynamix.formbuilder.contact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.dynamix.formbuilder.databinding.DynamixContactSuggestionListItemBinding

/**
 * Created by user on 11/3/20
 */
class DynamixContactSuggestionAdapter(
    context: Context,
    resource: Int,
    private val data: MutableList<com.dynamix.formbuilder.contact.DynamixContact>
) : ArrayAdapter<com.dynamix.formbuilder.contact.DynamixContact>(context, resource, data) {

    private val suggestions = mutableListOf<com.dynamix.formbuilder.contact.DynamixContact>()

    // copy the original data as the adapter will modify the original data
    private val tempData = data.toList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: DynamixContactSuggestionListItemBinding = if (convertView == null) {
            DynamixContactSuggestionListItemBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        } else {
            convertView.tag as DynamixContactSuggestionListItemBinding
        }
        val contact = data[position]
        binding.csLiName.text = contact.name
        binding.csLiPhone.text = contact.number
        // add the tag so DataBindingMapper works properly
        binding.root.tag = binding
        return binding.root
    }

    override fun getItem(position: Int): com.dynamix.formbuilder.contact.DynamixContact {
        return data[position]
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val contact = resultValue as com.dynamix.formbuilder.contact.DynamixContact
            return contact.number
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                // search through the data
                for (contact in tempData) {
                    if (contact.number.contains(constraint)) {
                        suggestions.add(contact)
                    }
                }
                val results = FilterResults()
                results.values = suggestions
                results.count = suggestions.size
                results
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            if (results.count > 0) {
                val filteredContacts = results.values as List<com.dynamix.formbuilder.contact.DynamixContact>
                clear()
                addAll(filteredContacts)
            } else {
                clear()
            }
            notifyDataSetChanged()
        }
    }
}