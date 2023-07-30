package com.baseballmatching.app.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRef {

        companion object {
            val database = Firebase.database
            val usersRef = database.getReference("users")
            val gamesRef = database.getReference("games")
            val teamsRef = database.getReference("teams")
            val userLikeRef = database.getReference("userLike")
            val matchingRegistrationRef = database.getReference("matching_registration")
            val userLikeReceivedRef = database.getReference("userLikeReceived")
            val matchingCompleteRef = database.getReference("matching_complete")
        }
}