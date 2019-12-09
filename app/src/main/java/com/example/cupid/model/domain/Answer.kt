package com.example.cupid.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Answer (
    val questionId : Int = -1,
    val answerId : Int = -1
) : Parcelable