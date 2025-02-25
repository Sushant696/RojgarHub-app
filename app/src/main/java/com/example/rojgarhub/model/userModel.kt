package com.example.rojgarhub.model

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    var userId: String = "",
    var email: String = "",
    var firstName: String = "",
    var address: String = "",
    var phoneNumber: String = "",
    var role: String = "jobseeker"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "jobseeker"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(address)
        parcel.writeString(phoneNumber)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}