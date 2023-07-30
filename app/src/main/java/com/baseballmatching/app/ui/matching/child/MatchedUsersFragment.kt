package com.baseballmatching.app.ui.matching.child

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentMatchedUsersBinding
import com.baseballmatching.app.datamodel.LikeReceivedItem
import com.baseballmatching.app.datamodel.MatchingUserItem
import com.baseballmatching.app.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

var matchedUserList: MutableList<MatchingUserItem> = mutableListOf()
class MatchedUsersFragment: Fragment() {
    private val TAG = "MatchedUsersFragment"
    private lateinit var matchingAdapter: MatchingAdapter
    private lateinit var binding: FragmentMatchedUsersBinding
    //private val currentUserId = Firebase.auth.currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matched_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMatchedUsersBinding.bind(view)

        matchingAdapter = MatchingAdapter(true)
        binding.rvUserLikeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchingAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        getMatchedUserList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")

        matchedUserList.clear()
    }

    private fun getMatchedUserList() {
        val currentUserId = Firebase.auth.currentUser?.uid.toString()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                matchedUserList.clear()
                val matchedUserList2 = dataSnapshot.children.map {
                    it.getValue(MatchingUserItem::class.java)!!
                    }
                    matchingAdapter.submitList(matchedUserList2)
                    Log.d(TAG, "matchedUserList2: $matchedUserList2")
                    Log.d(TAG, "currentUserId: $currentUserId")
                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FirebaseRef.matchingCompleteRef.child(currentUserId).addValueEventListener(postListener)
    }
}