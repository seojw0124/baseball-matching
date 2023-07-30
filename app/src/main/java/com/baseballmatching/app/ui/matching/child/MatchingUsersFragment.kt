package com.baseballmatching.app.ui.matching.child

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentMatchingUsersBinding
import com.baseballmatching.app.datamodel.MatchingUserItem
import com.baseballmatching.app.ui.GameDetailActivity
import com.baseballmatching.app.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MatchingUsersFragment: Fragment() {
    private lateinit var binding: FragmentMatchingUsersBinding
    private lateinit var matchingAdapter: MatchingAdapter
    private val currentUserId = Firebase.auth.currentUser?.uid.toString()
    private var userLikeList: MutableList<MatchingUserItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MatchingUsersFragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_matching_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MatchingUsersFragment", "onViewCreated")

        binding = FragmentMatchingUsersBinding.bind(view)

        matchingAdapter = MatchingAdapter(true)
        binding.rvUserLikeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchingAdapter
        }

        getUserLikeList()
    }

    override fun onStart() {
        super.onStart()
        Log.d("MatchingUsersFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MatchingUsersFragment", "onResume")
        matchingAdapter.submitList(userLikeList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MatchingUsersFragment", "onDestroyView")

        userLikeList.clear()
    }

    private fun getUserLikeList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    Log.d("MatchingUsersFragment", "dataModel: $dataModel")

                    val userLikeItem = dataModel.getValue()
                    for (item in userLikeItem as HashMap<*, *>) {
                        val gson = Gson()
                        val jsonString = gson.toJson(item.value)
                        val matchingUserItem = gson.fromJson(jsonString, MatchingUserItem::class.java)
                        userLikeList.add(matchingUserItem)
                    }
                    matchingAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FirebaseRef.userLikeRef.child(currentUserId).addValueEventListener(postListener)
    }
}