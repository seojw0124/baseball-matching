package com.baseballmatching.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.database.DBKey
import com.baseballmatching.app.database.DBKey.Companion.DB_MATCHING_LIST
import com.baseballmatching.app.databinding.ActivityGameDetailBinding
import com.baseballmatching.app.datamodel.GameDetail
import com.baseballmatching.app.datamodel.MatchingUserItem
import com.baseballmatching.app.ui.home.HomeAdapter
import com.baseballmatching.app.utils.FirebaseRef
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GameDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var gameDetailAdapter: GameDetailAdapter
    private var matchingList: MutableList<MatchingUserItem> = mutableListOf()
    private val TAG = "GameDetailActivity"
    private lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val model = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("game", GameDetail::class.java)
        } else {
            intent.getParcelableExtra<GameDetail>("game")
        }

        gameId = model?.Game?.id.toString()

        Glide.with(binding.ivBallpark.context)
            .load(model?.ballparkImage)
            .into(binding.ivBallpark)
        binding.tvGameTitle.text = getString(R.string.game_detail_title, model?.Game?.home, model?.Game?.away)
        binding.tvGameDate.text = getString(R.string.game_detail_date, model?.Game?.time?.get(6), model?.Game?.time?.substring(8, 10), model?.Game?.time?.substring(11, 13), model?.Game?.time?.substring(14, 16))
        binding.tvBallpark.text = model?.ballpark

        /*val currentMatchingList = Firebase.database.reference.child(DB_MATCHING_LIST).child(model?.Game?.id.toString())

        currentMatchingList.get().addOnSuccessListener {
            val matchingUserItem = it.getValue(MatchingUserItem::class.java)
            if (matchingUserItem != null) {
                matchingList.add(matchingUserItem)
            }
            gameDetailAdapter.submitList(matchingList)
        }
        currentMatchingList.get().addOnFailureListener {
            Log.d("GameDetailActivity", "Failed to get matching list")
        }*/

        binding.btnRegisterMatchingUser.setOnClickListener {
            for (matchingUserItem in matchingList) {
                if (matchingUserItem.userId == Firebase.auth.currentUser?.uid) {
                    Snackbar.make(binding.root, "이미 매칭 신청을 하셨습니다.", Snackbar.LENGTH_SHORT).show()

                    return@setOnClickListener
                }
            }

            val intent = Intent(this, MatchingRegistrationActivity::class.java)
            intent.putExtra("gameId", model?.Game?.id)
            startActivity(intent)
        }

        //getMatchingList(model?.Game?.id.toString())

        gameDetailAdapter = GameDetailAdapter()
        binding.rvMatchingUserList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = gameDetailAdapter
        }
    }

    //생명주기
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: $matchingList")
        getMatchingList(gameId)
    }

    override fun onResume() {
        super.onResume()
        //gameDetailAdapter.notifyDataSetChanged()
        matchingList = mutableListOf()
        Log.d(TAG, "onResume: ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: ")
    }

    override fun onDestroy() {
        super.onDestroy()

        matchingList = mutableListOf()
        Log.d(TAG, "onDestroy: ")
    }

    private fun getMatchingList(gameId: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {

                    val matchingUserItem = dataModel.getValue(MatchingUserItem::class.java)
                    matchingList.add(matchingUserItem!!)

                    gameDetailAdapter.submitList(matchingList.sortedByDescending { it.dateTime })
                }

                gameDetailAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.matchingListRef.child(gameId).addValueEventListener(postListener)

    }

}