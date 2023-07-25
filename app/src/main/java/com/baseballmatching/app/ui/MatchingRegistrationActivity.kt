package com.baseballmatching.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baseballmatching.app.MainActivity
import com.baseballmatching.app.database.DBKey
import com.baseballmatching.app.database.DBKey.Companion.DB_MATCHING_LIST
import com.baseballmatching.app.databinding.ActivityMatchingRegistrationBinding
import com.baseballmatching.app.datamodel.UserItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchingRegistrationActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMatchingRegistrationBinding
    private lateinit var userName: String
    private lateinit var age: String
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMatchingRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameId = intent.getIntExtra("gameId", -1)

        val currentUserId = Firebase.auth.currentUser?.uid ?: ""
        val currentUserDB = Firebase.database.reference.child(DBKey.DB_USERS).child(currentUserId)

        currentUserDB.get().addOnSuccessListener {
            val currentUserItem = it.getValue(UserItem::class.java) ?: return@addOnSuccessListener

            userName = currentUserItem.userName.toString()
            age = currentUserItem.age.toString()
            gender = currentUserItem.gender.toString()

            binding.etCurrentUserName.setText(userName)
            binding.etCurrentUserAge.setText(age)
            binding.etCurrentUserGender.setText(gender)
        }

        binding.btnRegisterMatchingUser.setOnClickListener {
            val preferredSeat = binding.etCurrentUserPreferredSeat.text.toString()

            if (preferredSeat.isEmpty()) {
                return@setOnClickListener
            }

            val matchingUser = mutableMapOf<String, Any>()
            matchingUser["userId"] = currentUserId
            matchingUser["userName"] = userName
            matchingUser["age"] = age
            matchingUser["gender"] = gender
            matchingUser["preferredSeat"] = preferredSeat
            matchingUser["dateTime"] = System.currentTimeMillis()

            Firebase.database.reference.child(DB_MATCHING_LIST).child(gameId.toString()).child(currentUserId).updateChildren(matchingUser).addOnSuccessListener {
                    Toast.makeText(this, "매칭 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "매칭 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("firebase matching_list", "matching_list data update failed")
                }

            // 매칭 등록 후 이전 화면으로 돌아가기
            finish()
        }
    }
}