package com.baseballmatching.app.datamodel

import com.google.gson.annotations.SerializedName

data class TeamData(
    @SerializedName("teams") val teams: List<Team>
)
