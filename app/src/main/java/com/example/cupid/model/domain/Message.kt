package com.example.cupid.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Constructor
import java.security.acl.Owner

@Parcelize
data class Message (
    val createdTime : Long = 0,
    val owner : Account,
    val payload : String,
    val readyBy : ArrayList<Account>
) : Parcelable {
    constructor(owner : Account, payload: String) :
            this(System.currentTimeMillis(), owner, payload, ArrayList<Account>())
}