package com.dynamix.formbuilder.contact

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dynamix.core.adapter.DynamixGenericRecyclerAdapter
import com.dynamix.core.view.DynamixBaseBottomSheet
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.databinding.DynamixLayoutMultiNumberBottomSheetBinding
import com.dynamix.formbuilder.databinding.DynamixRowMultiNumberPickerListBinding
import imageview.avatar.com.avatarimageview.UserAvatar

class DynamixMultiContactBottomSheet(
    private val contacts: List<DynamixContact>,
    private val clickListener: ItemClickListener
) : DynamixBaseBottomSheet("Choose phone number") {

    private val binding: DynamixLayoutMultiNumberBottomSheetBinding
        get() = _binding!!
    private var _binding: DynamixLayoutMultiNumberBottomSheetBinding? = null

    override val layoutView: View
        get() {
            _binding = DynamixLayoutMultiNumberBottomSheetBinding.inflate(
                LayoutInflater.from(requireContext()),
                null,
                false
            )
            return binding.root
        }

    override fun setupViews() {
        binding.ltMtNbBsContactLayout.rwCtPkLtImage.setAvatar(UserAvatar("", contacts[0].name))
        binding.ltMtNbBsContactLayout.rwCtPkLtName.text = contacts[0].name
        binding.ltMtNbBsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ltMtNbBsRecyclerView.adapter = DynamixGenericRecyclerAdapter(
            contacts, R.layout.dynamix_row_multi_number_picker_list
        ) { binding: DynamixRowMultiNumberPickerListBinding, item: DynamixContact, _: List<DynamixContact> ->
            binding.rwMtNbPkPhone.text = item.number
            binding.root.setOnClickListener {
                clickListener.onItemClick(item)
                dismiss()
            }
        }
    }

    fun interface ItemClickListener {
        fun onItemClick(item: DynamixContact)
    }
}