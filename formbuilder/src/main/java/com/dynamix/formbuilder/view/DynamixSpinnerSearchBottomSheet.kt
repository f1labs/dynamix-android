package com.dynamix.formbuilder.view

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dynamix.core.adapter.DynamixGenericRecyclerAdapter
import com.dynamix.core.utils.DynamixListItemFilter
import com.dynamix.core.view.DynamixBaseBottomSheet
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.databinding.DynamixLayoutSpinnerSearchBottomSheetBinding
import com.dynamix.formbuilder.databinding.DynamixRowSpinnerSearchBinding
import imageview.avatar.com.avatarimageview.UserAvatar
import io.reactivex.disposables.CompositeDisposable

class DynamixSpinnerSearchBottomSheet(
    private val title: String,
    private val groupList: Array<String>,
    clickListener: ItemClickListener
) : DynamixBaseBottomSheet(title) {

    private val binding: DynamixLayoutSpinnerSearchBottomSheetBinding
        get() = _binding!!
    private var _binding: DynamixLayoutSpinnerSearchBottomSheetBinding? = null

    private val clickListener: ItemClickListener
    private val bag = CompositeDisposable()

    init {
        this.clickListener = clickListener
    }


    override val layoutView: View
        get() {
            _binding = DynamixLayoutSpinnerSearchBottomSheetBinding.inflate(
                LayoutInflater.from(requireContext()),
                null,
                false
            )
            val searchIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.dynamix_ic_search,
                binding.ltCtPkBsRoot.context.theme
            )
            DrawableCompat.setTint(
                searchIcon!!,
                ResourcesCompat.getColor(
                    resources,
                    R.color.material_on_surface_emphasis_medium,
                    binding.ltCtPkBsRoot.context.theme
                )
            )
            binding.ltSpSrBsSearch.setCompoundDrawablesWithIntrinsicBounds(
                searchIcon,
                null,
                null,
                null
            )
            return binding.root
        }

    override fun setupViews() {
        binding.ltSpSrBsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ltSpSrBsRecyclerView.adapter = DynamixGenericRecyclerAdapter(
            groupList.toList(), R.layout.dynamix_row_spinner_search
        ) { binding: DynamixRowSpinnerSearchBinding, item: String, _: List<String> ->
            binding.rwCtPkLtImage.setAvatar(UserAvatar("", item))
            binding.rwCtPkLtName.text = item
            binding.root.setOnClickListener {
                clickListener.onItemClick(item)
                dismiss()
            }
        }
        setupFilter()
        setupEditTextExpansion(binding.ltSpSrBsSearch)
    }

    private fun setupFilter() {
        val filter =
            DynamixListItemFilter(
                groupList.toList(),
                object : DynamixListItemFilter.FilterCallback<String> {
                    override fun getPredicate(item: String, pattern: String): Boolean {
                        return item.lowercase().contains(pattern.lowercase())
                    }

                    override fun publishResults(items: List<String>) {
                        (binding.ltSpSrBsRecyclerView.adapter as DynamixGenericRecyclerAdapter<String, DynamixRowSpinnerSearchBinding>).refreshData(
                            items
                        )
                        handleEmptyList(items)
                    }
                })
        binding.ltSpSrBsSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filter.filter(s.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun handleEmptyList(items: List<String>) {
        binding.ltSpSrBsEmptyView.isVisible = items.isEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bag.clear()
    }

    fun interface ItemClickListener {
        fun onItemClick(item: String)
    }
}