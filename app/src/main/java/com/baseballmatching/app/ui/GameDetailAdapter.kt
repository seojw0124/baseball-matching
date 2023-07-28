package com.baseballmatching.app.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.ItemMatchingUserBinding
import com.baseballmatching.app.datamodel.MatchingUserItem

class GameDetailAdapter: ListAdapter<MatchingUserItem, GameDetailAdapter.ViewHolder>(diffUtil) {

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: ItemMatchingUserBinding): RecyclerView.ViewHolder(binding.root) {
        private var favorite = false
        fun bind(matchingUser: MatchingUserItem) {
            favorite = matchingUser.favorite ?: false

            binding.tvUserName.text = matchingUser.userName
            binding.tvUserAgeAndGender.text = "${matchingUser.age} / ${matchingUser.gender}"
            binding.tvUserPreferredSeat.text = "선호 좌석: ${matchingUser.preferredSeat}"
            if (matchingUser.favorite == true) {
                binding.ivLike.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.ivLike.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }

        init {
            binding.ivLike.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                if (favorite) {
                    binding.ivLike.setImageResource(R.drawable.baseline_favorite_24)
                } else {
                    binding.ivLike.setImageResource(R.drawable.baseline_favorite_border_24)
                }
            }
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