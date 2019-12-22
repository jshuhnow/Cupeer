package com.example.cupid.model.domain

import android.net.MacAddress
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account (
    var name : String = "",
    var avatarId : Int = 0,
    val answers: ArrayList<Answer> = ArrayList<Answer>()
) : Parcelable
