package com.baseballmatching.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baseballmatching.app.databinding.ItemMatchingUserBinding
import com.baseballmatching.app.datamodel.MatchingUserItem

class GameDetailAdapter: ListAdapter<MatchingUserItem, GameDetailAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMatchingUserBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(matchingUser: MatchingUserItem) {
            binding.tvUserName.text = matchingUser.userName
            binding.tvUserGender.text = matchingUser.gender
            binding.tvUserPreferredSeat.text = "선호 좌석: ${matchingUser.preferredSeat}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMatchingUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MatchingUserItem>() {
            override fun areItemsTheSame(oldItem: MatchingUserItem, newItem: MatchingUserItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: MatchingUserItem, newItem: MatchingUserItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}