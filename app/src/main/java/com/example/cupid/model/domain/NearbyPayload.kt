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
                Account::class.java.classLoader
            )
        } else if (type == "Answer") {
            this.obj = parcel.readParcelable<Answer>(
                Answer::class.java.classLoader
            )
        } else if (type== "Message") {
            this.obj = parcel.readParcelable(
                Message::class.java.classLoader
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
        parcel.writeParcelable(obj, flags)
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
