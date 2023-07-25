package com.baseballmatching.app.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRef {

        companion object {
            val database = Firebase.database
            val usersRef = database.getReference("users")
            val gamesRef = database.getReference("games")
            val teamsRef = database.getReference("teams")
            val matchingListRef = database.getReference("matching_list")

        }
}