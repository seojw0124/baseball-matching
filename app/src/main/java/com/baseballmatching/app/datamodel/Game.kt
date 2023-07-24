package com.baseballmatching.app.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Game(
    val id: Int? = null,
    val home: String? = null,
    val away: String? = null,
    val time: String? = null,
): Parcelable