package com.example.cupid.model.domain

import android.net.MacAddress
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account (
    //val macAddr : MacAddress,
    var name : String = "",
    //val age : Int = 0,
    var avatarId : Int = 0,
    val answers: ArrayList<Answer> = ArrayList<Answer>()
    //val photoPath : String = "",
    //val bio : String = "",
    //val pub_key : String = "",
    //val pri_key : String = ""
) : Parcelable