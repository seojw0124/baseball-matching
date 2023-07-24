package com.baseballmatching.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.ActivityGameDetailBinding
import com.baseballmatching.app.datamodel.GameDetail

class GameDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val model = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("game", GameDetail::class.java)
        } else {
            intent.getParcelableExtra<GameDetail>("game")
        }

        binding.tvGameTitle.text = getString(R.string.title_game_detail, model?.Game?.home, model?.Game?.away)
        binding.tvGameDate.text = getString(R.string.date_game_detail, model?.Game?.time?.get(6), model?.Game?.time?.substring(8, 10), model?.Game?.time?.substring(11, 13), model?.Game?.time?.substring(14, 16))
        binding.tvBallpark.text = model?.ballpark
    }
}