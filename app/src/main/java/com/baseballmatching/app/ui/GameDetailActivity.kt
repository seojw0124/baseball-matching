package com.baseballmatching.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.ActivityGameDetailBinding
import com.baseballmatching.app.datamodel.GameDetail
import com.baseballmatching.app.datamodel.MatchingRegistration
import com.baseballmatching.app.utils.FirebaseRef
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.UUID

class GameDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var gameDetailAdapter: GameDetailAdapter
    private var matchingList: MutableList<MatchingRegistration> = mutableListOf()
    private val TAG = "GameDetailActivity"
    private lateinit var gameId: String
    val currentUserId = Firebase.auth.currentUser?.uid.toString()
    private lateinit var otherUserId: String

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

        gameDetailAdapter = GameDetailAdapter(currentUserId)
        binding.rvMatchingUserList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = gameDetailAdapter
        }
        gameDetailAdapter.itemClickListener = object : GameDetailAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = matchingList[position]
                if (item.userId != currentUserId) {
                    otherUserId = item.userId.toString()

                    Log.d(TAG, "onItemClick: $item.likeList}")

                    if (item.likeList?.contains(currentUserId) == true) {
                        userLikeOtherUser(item, model, true)
                    } else {
                        userLikeOtherUser(item, model, false)
                    }
                } else {
                    Toast.makeText(this@GameDetailActivity, "본인은 좋아요 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
    }

    //생명주기
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
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

        //matchingList = mutableListOf()
        Log.d(TAG, "onDestroy: ")
    }

    private fun getMatchingList(gameId: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {

                    val matchingUserItem = dataModel.getValue(MatchingRegistration::class.java)
                    Log.d(TAG, "dataModel: $dataModel")
                    Log.d(TAG, "matchingUserItem: ${matchingUserItem?.likeList}")
                    matchingList.add(matchingUserItem!!)
                }

                gameDetailAdapter.submitList(matchingList)

                //gameDetailAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.matchingRegistrationRef.child(gameId).addValueEventListener(postListener)
    }

    private fun getUserLikeList(matchingUserItem: MatchingRegistration) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FirebaseRef.userLikeRef.child(currentUserId).addValueEventListener(postListener)
    }




    private fun userLikeOtherUser(matchingUserItem: MatchingRegistration, model: GameDetail?, isLiked: Boolean) {

        if (!isLiked) {
            val matchingUser = mutableMapOf<String, Any>()

            matchingUser["userId"] = matchingUserItem.userId.toString()
            matchingUser["userName"] = matchingUserItem.userName.toString()
            matchingUser["age"] = matchingUserItem.age?.toInt()!!
            matchingUser["gender"] = matchingUserItem.gender.toString()
            matchingUser["preferredSeat"] = matchingUserItem.preferredSeat.toString()
            matchingUser["dateTime"] = System.currentTimeMillis()

            val likeReceiveUser = mutableMapOf<String, Any>()
            likeReceiveUser["userId"] = currentUserId

            if (model != null) {
                matchingUser["gameInfo"] = model.Game
                likeReceiveUser["gameInfo"] = model.Game
            }
            FirebaseRef.userLikeRef.child(currentUserId).child(gameId).child(matchingUserItem.userId.toString()).setValue(matchingUser)
                .addOnSuccessListener {
                    FirebaseRef.userLikeReceivedRef.child(matchingUserItem.userId.toString()).child(gameId+"_"+currentUserId).setValue(likeReceiveUser)
                    FirebaseRef.matchingRegistrationRef.child(gameId).child(matchingUserItem.userId.toString()).child("likeList").child(currentUserId).setValue(currentUserId)
                Toast.makeText(this, "매칭을 신청하였습니다.", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                Toast.makeText(this, "매칭 신청에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            FirebaseRef.userLikeRef.child(currentUserId).child(gameId).child(matchingUserItem.userId.toString()).setValue(null)
                .addOnSuccessListener {
                    FirebaseRef.userLikeReceivedRef.child(matchingUserItem.userId.toString()).child(gameId+"_"+currentUserId).setValue(null)
                    FirebaseRef.matchingRegistrationRef.child(gameId).child(matchingUserItem.userId.toString()).child("likeList").child(currentUserId).setValue(null)
                Toast.makeText(this, "매칭 신청을 취소하였습니다.", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(this, "매칭 신청 취소에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getOtherUserLikeList(otherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message

            }
        }
        FirebaseRef.userLikeRef.child(otherUserId).addValueEventListener(postListener)
    }
}