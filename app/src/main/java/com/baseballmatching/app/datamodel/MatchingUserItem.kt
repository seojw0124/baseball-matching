package com.baseballmatching.app.datamodel

data class MatchingUserItem(
    val userId: String? = null,
    val userName: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val preferredSeat: String? = null,
    val dateTime: Long? = null,
    val likeList: Map<String, String>? = null,
    var gameInfo: Game? = null,
)
