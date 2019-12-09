package com.example.cupid.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message (
    val createdTime : Long = 0,
    val owner : Account,
    val payload : String,
    val readyBy : ArrayList<Account>
) : Parcelable