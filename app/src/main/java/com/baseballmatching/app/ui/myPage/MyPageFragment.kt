package com.baseballmatching.app.ui.myPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baseballmatching.app.R
import com.baseballmatching.app.database.DBKey.Companion.DB_USERS
import com.baseballmatching.app.databinding.FragmentMyPageBinding
import com.baseballmatching.app.datamodel.UserItem
import com.baseballmatching.app.ui.SignInActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyPageFragment: Fragment() {

    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyPageBinding.bind(view)

        val currentUserId = Firebase.auth.currentUser?.uid ?: ""
        val currentUserDB = Firebase.database.reference.child(DB_USERS).child(currentUserId)

        currentUserDB.get().addOnSuccessListener {
            val currentUserItem = it.getValue(UserItem::class.java) ?: return@addOnSuccessListener
            binding.etEmail.setText(currentUserItem.email)
            binding.etEmail.isEnabled = false
            binding.etUserName.setText(currentUserItem.userName)
            binding.etAge.setText(currentUserItem.age)
            binding.etGender.setText(currentUserItem.gender)
        }

        binding.btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(context, SignInActivity::class.java))
            activity?.finish()
        }
    }
}