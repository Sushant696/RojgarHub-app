package com.example.rojgarhub.model

import android.os.Parcel
import android.os.Parcelable

data class ApplicationModel(
    val applicationId: String = "",
    val jobId: String = "",
    val userId: String = "",
    val jobTitle: String = "",
    val applicantName: String = "",
    val applicantEmail: String = "",
    val applicantPhone: String = "",
    val applicationDate: Long = System.currentTimeMillis(),
    val status: String = "pending"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "pending"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(applicationId)
        parcel.writeString(jobId)
        parcel.writeString(userId)
        parcel.writeString(jobTitle)
        parcel.writeString(applicantName)
        parcel.writeString(applicantEmail)
        parcel.writeString(applicantPhone)
        parcel.writeLong(applicationDate)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ApplicationModel> {
        override fun createFromParcel(parcel: Parcel): ApplicationModel {
            return ApplicationModel(parcel)
        }

        override fun newArray(size: Int): Array<ApplicationModel?> {
            return arrayOfNulls(size)
        }
    }
}