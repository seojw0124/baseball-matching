package com.baseballmatching.app.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDetail(
    val Game: Game,
    val homeLogo: String? = null,
    val awayLogo: String? = null,
    val ballpark: String? = null,
    val time: String? = null,
    val ballparkImage: String? = null,
): Parcelable
