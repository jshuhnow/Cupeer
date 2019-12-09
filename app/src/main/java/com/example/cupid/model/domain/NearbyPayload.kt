package com.example.cupid.model.domain

import android.os.Parcel
import android.os.Parcelable
import android.util.Log


class NearbyPayload : Parcelable {
    var type: String? = null
    var obj: Parcelable? = null

    constructor(parcel: Parcel) {
        type = parcel.readString()!!

        if (type == "Account") {
            this.obj = parcel.readParcelable<Account>(
                Account::javaClass.javaClass.classLoader
            )!!
        } else if (type == "Answer") {
            this.obj = parcel.readParcelable<Answer>(
                Answer::javaClass.javaClass.classLoader
            )
        } else if (type== "Message") {
            this.obj = parcel.readParcelable<Message>(
                Message::javaClass.javaClass.classLoader
            )
        } else {
            Log.d(TAG, "Unknown Type Error")
        }
    }

    constructor(type : String, obj : Parcelable) {
        this.type = type
        this.obj = obj
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NearbyPayload> {
        override fun createFromParcel(parcel: Parcel): NearbyPayload {
            return NearbyPayload(parcel)
        }

        override fun newArray(size: Int): Array<NearbyPayload?> {
            return arrayOfNulls(size)
        }
        val TAG = "NearbyPayload"

    }

}
