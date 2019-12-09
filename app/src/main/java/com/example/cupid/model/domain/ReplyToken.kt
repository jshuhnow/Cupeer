package com.example.cupid.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReplyToken (
    val isAccepted : Boolean,
    val stage : Int
) : Parcelable