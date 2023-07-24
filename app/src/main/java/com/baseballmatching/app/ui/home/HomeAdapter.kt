package com.baseballmatching.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baseballmatching.app.databinding.ItemGameBinding
import com.baseballmatching.app.datamodel.Game
import com.baseballmatching.app.datamodel.GameDetail
import com.bumptech.glide.Glide

class HomeAdapter(private val onClick: (GameDetail) -> Unit): ListAdapter<GameDetail, HomeAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemGameBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(gameDetail: GameDetail) {
            /*if (gameDetail.homeLogo != null) {
                Glide.with(binding.ivBannerGameHomeTeamLogo.context)
                    .load(gameDetail.homeLogo)
                    .into(binding.ivBannerGameHomeTeamLogo)
            }
            if (gameDetail.awayLogo != null) {
                Glide.with(binding.ivBannerGameAwayTeamLogo.context)
                    .load(gameDetail.awayLogo)
                    .into(binding.ivBannerGameAwayTeamLogo)
            }*/
            Glide.with(binding.ivBannerGameHomeTeamLogo.context)
                .load(gameDetail.homeLogo)
                .into(binding.ivBannerGameHomeTeamLogo)
            Glide.with(binding.ivBannerGameAwayTeamLogo.context)
                .load(gameDetail.awayLogo)
                .into(binding.ivBannerGameAwayTeamLogo)
            binding.tvBannerGameHomeTeamName.text = gameDetail.Game.home
            binding.tvBannerGameAwayTeamName.text = gameDetail.Game.away
            binding.tvBannerGameBallpark.text = gameDetail.ballpark
            binding.tvBannerGameTime.text = gameDetail.time?.substring(0, 5)



            binding.root.setOnClickListener {
                onClick(gameDetail)
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
        val diffUtil = object: DiffUtil.ItemCallback<GameDetail>() {
            override fun areItemsTheSame(oldItem: GameDetail, newItem: GameDetail): Boolean {
                return oldItem.Game.id == newItem.Game.id
            }

            override fun areContentsTheSame(oldItem: GameDetail, newItem: GameDetail): Boolean {
                return oldItem == newItem
            }
        }
    }

}