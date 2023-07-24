package com.baseballmatching.app.datamodel

import com.google.gson.annotations.SerializedName

data class Team(
    @SerializedName("team_name") val teamName: String? = null,
    val ballpark: String? = null,
    val location: String? = null,
    val logo: String? = null,
)
