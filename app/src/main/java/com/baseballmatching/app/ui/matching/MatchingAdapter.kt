package com.baseballmatching.app.ui.matching

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baseballmatching.app.databinding.ItemMatchingRegistrationBinding
import com.baseballmatching.app.datamodel.MatchingRegistration

class MatchingAdapter(private val onClick: (MatchingRegistration) -> Unit): ListAdapter<MatchingRegistration, MatchingAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMatchingRegistrationBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(matchingUser: MatchingRegistration) {

            binding.root.setOnClickListener {
                onClick(matchingUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMatchingRegistrationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MatchingRegistration>() {
            override fun areItemsTheSame(oldItem: MatchingRegistration, newItem: MatchingRegistration): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: MatchingRegistration, newItem: MatchingRegistration): Boolean {
                return oldItem == newItem
            }
        }
    }
}