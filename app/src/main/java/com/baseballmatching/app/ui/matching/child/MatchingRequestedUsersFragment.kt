package com.baseballmatching.app.ui.matching.child

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentMatchingUsersBinding
import com.baseballmatching.app.datamodel.LikeReceivedItem
import com.baseballmatching.app.datamodel.MatchingRegistration
import com.baseballmatching.app.datamodel.MatchingUserItem
import com.baseballmatching.app.ui.GameDetailAdapter
import com.baseballmatching.app.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MatchingRequestedUsersFragment: Fragment() {
    private lateinit var binding: FragmentMatchingUsersBinding
    private val TAG = "MatchingRequestedUsersFragment"
    private lateinit var matchingAdapter: MatchingAdapter
    private val currentUserId = Firebase.auth.currentUser?.uid.toString()
    var requestedMatchingUserList: MutableList<MatchingUserItem> = mutableListOf()
    var userLikeList: MutableList<MatchingUserItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matching_requested_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MatchingUsersFragment", "onViewCreated")

        binding = FragmentMatchingUsersBinding.bind(view)

        matchingAdapter = MatchingAdapter(false)
        binding.rvUserLikeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchingAdapter
        }

        getOtherUserLikeList()
    }

    override fun onStart() {
        super.onStart()
        Log.d("MatchingUsersFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MatchingUsersFragment", "onResume")

        //matchingAdapter.submitList(requestedMatchingUserList)

        matchingAdapter.itemClickListener = object : MatchingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                getMatchingRegistration(requestedMatchingUserList[position], position)

                /*val item = requestedMatchingUserList[position]

                FirebaseRef.matchingCompleteRef.child(currentUserId).child(item.gameInfo?.id.toString()+"_"+item.userId.toString()).setValue(item)
                FirebaseRef.matchingCompleteRef.child(item.userId.toString()).child(item.gameInfo?.id.toString()+"_"+currentUserId).setValue(item)
                FirebaseRef.userLikeReceivedRef.child(currentUserId).child(item.gameInfo?.id.toString()+"_"+item.userId.toString()).removeValue()
                // remove from requestedMatchingUserList
                requestedMatchingUserList.removeAt(position)
                Log.d(TAG, "requestedMatchingUserList: $requestedMatchingUserList")
                // 리사이클러뷰 데이터 삭제 알림
                matchingAdapter.notifyItemRemoved(position)*/
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MatchingUsersFragment", "onDestroyView")

        requestedMatchingUserList.clear()
    }

    private fun getRequestedMatchingUserList(item: LikeReceivedItem) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val matchingUserItem = dataSnapshot.getValue(MatchingUserItem::class.java)
                matchingUserItem?.gameInfo = item.gameInfo

                Log.d(TAG, "matchingUserItem: $matchingUserItem")
                Log.d(TAG, "dataSnapshot: $dataSnapshot")

                requestedMatchingUserList.add(matchingUserItem!!)
                matchedUserList.add(matchingUserItem)
                matchingAdapter.submitList(requestedMatchingUserList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FirebaseRef.matchingRegistrationRef.child(item.gameInfo?.id.toString()).child(item.userId.toString()).addValueEventListener(postListener)
    }

    private fun getOtherUserLikeList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val likeReceivedItem = dataModel.getValue(LikeReceivedItem::class.java)
                    Log.d(TAG, "likeReceivedItem: $likeReceivedItem")

                    getRequestedMatchingUserList(likeReceivedItem!!)
                }
                //matchingAdapter.submitList(requestedMatchingUserList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "getOtherUserLikeList: onCancelled: ${databaseError.message}")
            }
        }
        FirebaseRef.userLikeReceivedRef.child(currentUserId).addValueEventListener(postListener)
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

    private fun getMatchingRegistration(item: MatchingUserItem, position: Int) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val matchingRegistration = dataSnapshot.getValue(MatchingUserItem::class.java)
                Log.d(TAG, "matchingRegistration: $matchingRegistration")
                if (matchingRegistration != null) {
                    matchingRegistration.gameInfo = item.gameInfo
                }

                FirebaseRef.matchingCompleteRef.child(currentUserId).child(item.gameInfo?.id.toString()+"_"+item.userId.toString()).setValue(item)
                FirebaseRef.matchingCompleteRef.child(item.userId.toString()).child(item.gameInfo?.id.toString()+"_"+currentUserId).setValue(matchingRegistration)
                FirebaseRef.matchingRegistrationRef.child(item.gameInfo?.id.toString()).child(item.userId.toString()).child("likeList").child(currentUserId).setValue(currentUserId)
                FirebaseRef.userLikeReceivedRef.child(currentUserId).child(item.gameInfo?.id.toString()+"_"+item.userId.toString()).removeValue()
                FirebaseRef.userLikeRef.child(item.userId.toString()).child(item.gameInfo?.id.toString()).child(currentUserId).removeValue()
                // remove from requestedMatchingUserList
                requestedMatchingUserList.removeAt(position)
                Log.d(TAG, "requestedMatchingUserList: $requestedMatchingUserList")
                // 리사이클러뷰 데이터 삭제 알림
                matchingAdapter.notifyItemRemoved(position)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FirebaseRef.matchingRegistrationRef.child(item.gameInfo?.id.toString()).child(currentUserId).addValueEventListener(postListener)
    }
}