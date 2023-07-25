package com.baseballmatching.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.database.DBKey
import com.baseballmatching.app.database.DBKey.Companion.DB_MATCHING_LIST
import com.baseballmatching.app.databinding.ActivityGameDetailBinding
import com.baseballmatching.app.datamodel.GameDetail
import com.baseballmatching.app.datamodel.MatchingUserItem
import com.baseballmatching.app.ui.home.HomeAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GameDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var gameDetailAdapter: GameDetailAdapter
    private var matchingList: MutableList<MatchingUserItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val model = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("game", GameDetail::class.java)
        } else {
            intent.getParcelableExtra<GameDetail>("game")
        }

        Glide.with(binding.ivBallpark.context)
            .load(model?.ballparkImage)
            .into(binding.ivBallpark)
        binding.tvGameTitle.text = getString(R.string.game_detail_title, model?.Game?.home, model?.Game?.away)
        binding.tvGameDate.text = getString(R.string.game_detail_date, model?.Game?.time?.get(6), model?.Game?.time?.substring(8, 10), model?.Game?.time?.substring(11, 13), model?.Game?.time?.substring(14, 16))
        binding.tvBallpark.text = model?.ballpark

        val currentMatchingList = Firebase.database.reference.child(DB_MATCHING_LIST).child(model?.Game?.id.toString())

        currentMatchingList.get().addOnSuccessListener {
            val matchingUserItem = it.getValue(MatchingUserItem::class.java)
            if (matchingUserItem != null) {
                matchingList.add(matchingUserItem)
            }
            gameDetailAdapter.submitList(matchingList)
        }
        currentMatchingList.get().addOnFailureListener {
            Log.d("GameDetailActivity", "Failed to get matching list")
        }

        gameDetailAdapter = GameDetailAdapter()
        Log.d("GameDetailActivity", "matchingUserList: $matchingList")

        binding.rvMatchingUserList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = gameDetailAdapter
        }


        binding.btnRegisterMatchingUser.setOnClickListener {
            val intent = Intent(this, MatchingRegistrationActivity::class.java)
            intent.putExtra("gameId", model?.Game?.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        gameDetailAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        matchingList = mutableListOf()
    }
}