package com.baseballmatching.app.ui.matching.child

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.ItemMatchingUserBinding
import com.baseballmatching.app.datamodel.MatchingUserItem

class MatchingAdapter(var isLiked: Boolean): ListAdapter<MatchingUserItem, MatchingAdapter.ViewHolder>(
    diffUtil
) {
    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: ItemMatchingUserBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(matchingUser: MatchingUserItem) {
            if (isLiked) {
                binding.ivLike.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.ivLike.setImageResource(R.drawable.baseline_favorite_border_24)
            }
            binding.tvUserName.text = matchingUser.userName
            binding.tvUserAgeAndGender.text = "${matchingUser.age} / ${matchingUser.gender}"
            binding.tvUserPreferredSeat.text = "선호 좌석: ${matchingUser.preferredSeat}"
            binding.tvGameInfo.text =
                "${matchingUser.gameInfo?.time?.split("T")?.get(0)?.substring(5, 10)}   ${matchingUser.gameInfo?.home?.split(" ")?.get(0)} vs ${matchingUser.gameInfo?.away?.split(" ")?.get(0)}"

        }

        init {
            binding.ivLike.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
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