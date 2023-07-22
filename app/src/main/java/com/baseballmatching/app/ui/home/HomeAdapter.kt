package com.baseballmatching.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baseballmatching.app.databinding.ItemGameBinding
import com.baseballmatching.app.datamodel.Game

class HomeAdapter(private val onClick: (Game) -> Unit): ListAdapter<Game, HomeAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemGameBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            /*binding.nicknameTextView.text = userItem.nickname
            binding.statusMessageTextView.text = userItem.statusMessage*/

            binding.root.setOnClickListener {
                onClick(game)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem == newItem
            }
        }
    }

}