package com.example.cupid.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Constructor
import java.security.acl.Owner

@Parcelize
enum class MessageType : Parcelable {
    USER, SYSTEM
}

class SystemMessageString {
    companion object {
        const val GREENLIGHT = "GREENLIGHT"
        const val NO_GREENLIGHT = "NO_GREENLIGHT"
    }
}


@Parcelize
data class Message (
    val createdTime : Long = 0,
    val owner : Account,
    val payload : String,
    val type : MessageType,
    val readyBy : ArrayList<Account>
) : Parcelable {
    constructor(owner : Account, payload: String, type: MessageType = MessageType.USER) :
            this(System.currentTimeMillis(), owner, payload, type, ArrayList<Account>())
}