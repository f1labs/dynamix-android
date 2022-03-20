package com.dynamix.formbuilder.contact

import android.annotation.SuppressLint
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
import com.dynamix.core.utils.DynamixListItemFilter.FilterCallback
import com.dynamix.core.view.DynamixBaseBottomSheet
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.databinding.DynamixLayoutContactPickerBottomSheetBinding
import com.dynamix.formbuilder.databinding.DynamixRowContactPickerListBinding
import imageview.avatar.com.avatarimageview.UserAvatar
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.stream.Collectors

open class DynamixContactPickerBottomSheet(
    private val contactFetcher: com.dynamix.formbuilder.contact.DynamixContactFetcher,
    clickListener: ItemClickListener
) : DynamixBaseBottomSheet("Choose a Contact") {

    private val binding: DynamixLayoutContactPickerBottomSheetBinding
        get() = _binding!!
    private var _binding: DynamixLayoutContactPickerBottomSheetBinding? = null

    private val groupList: MutableList<List<com.dynamix.formbuilder.contact.DynamixContact>>
    private val clickListener: ItemClickListener
    private val bag = CompositeDisposable()

    init {
        groupList = ArrayList()
        this.clickListener = clickListener
    }

    private fun groupContactList(contacts: List<com.dynamix.formbuilder.contact.DynamixContact>): List<List<com.dynamix.formbuilder.contact.DynamixContact>> {
        val groups: Map<String, List<com.dynamix.formbuilder.contact.DynamixContact>> = contacts.stream()
            .sorted(
                Comparator.comparing(
                    { it.name },
                    java.lang.String.CASE_INSENSITIVE_ORDER
                )
            )
            .collect(
                Collectors.groupingBy(
                    { it.name },
                    { LinkedHashMap() },
                    Collectors.toList()
                )
            )
        return ArrayList(groups.values)
    }

    override val layoutView: View
        get() {
            _binding = DynamixLayoutContactPickerBottomSheetBinding.inflate(
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
            binding.ltCtPkBsSearch.setCompoundDrawablesWithIntrinsicBounds(
                searchIcon,
                null,
                null,
                null
            )
            return binding.root
        }

    override fun setupViews() {
        binding.ltCtPkBsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ltCtPkBsRecyclerView.adapter = DynamixGenericRecyclerAdapter(
            groupList, R.layout.dynamix_row_contact_picker_list
        ) { binding: DynamixRowContactPickerListBinding, item: List<com.dynamix.formbuilder.contact.DynamixContact>, _: List<List<com.dynamix.formbuilder.contact.DynamixContact>> ->
            binding.rwCtPkLtImage.setAvatar(UserAvatar("", item[0].name))
            binding.rwCtPkLtName.text = item[0].name
            binding.rwCtPkLtMultiIcon.isVisible = item.size != 1
            binding.root.setOnClickListener {
                if (item.size == 1) {
                    clickListener.onItemClick(item[0])
                } else {
                    // open bottom sheet for details
                    val bottomSheet = DynamixMultiContactBottomSheet(item) {
                        clickListener.onItemClick(it)
                    }
                    bottomSheet.showNow(requireActivity().supportFragmentManager, null)
                }
                dismiss()
            }
        }
        setupFilter()
        fetchContacts()
        setupEditTextExpansion(binding.ltCtPkBsSearch)
    }

    private fun fetchContacts() {
        // check if we already have fetched the contacts
        if (!contactFetcher.isContactFetched) {
            binding.ltCtPkBsProgress.show()
        }
        bag.add(
            contactFetcher.contactList
                .subscribe({
                    groupList.clear()
                    groupList.addAll(groupContactList(it))
                    refreshContacts()
                    binding.ltCtPkBsProgress.hide()
                }) {
                    binding.ltCtPkBsProgress.hide()
                    binding.ltCtPkBsEmptyView.text = it.localizedMessage
                })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshContacts() {
        binding.ltCtPkBsRecyclerView.adapter!!.notifyDataSetChanged()
        handleEmptyList(groupList)
    }

    private fun setupFilter() {
        val filter =
            DynamixListItemFilter<List<com.dynamix.formbuilder.contact.DynamixContact>>(groupList, object : FilterCallback<List<com.dynamix.formbuilder.contact.DynamixContact>> {
                override fun getPredicate(item: List<com.dynamix.formbuilder.contact.DynamixContact>, pattern: String): Boolean {
                    return item[0].name.lowercase().contains(pattern)
                }

                override fun publishResults(items: List<List<com.dynamix.formbuilder.contact.DynamixContact>>) {
                    (binding.ltCtPkBsRecyclerView.adapter as DynamixGenericRecyclerAdapter<List<com.dynamix.formbuilder.contact.DynamixContact>, DynamixRowContactPickerListBinding>)!!.refreshData(
                        items
                    )
                    handleEmptyList(items)
                }
            })
        binding.ltCtPkBsSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filter.filter(s.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun handleEmptyList(items: List<List<com.dynamix.formbuilder.contact.DynamixContact>>) {
        binding.ltCtPkBsEmptyView.isVisible = items.isEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bag.clear()
    }

    fun interface ItemClickListener {
        fun onItemClick(item: com.dynamix.formbuilder.contact.DynamixContact)
    }
}