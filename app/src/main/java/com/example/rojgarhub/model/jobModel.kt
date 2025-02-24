package com.example.rojgarhub.model

import android.os.Parcel
import android.os.Parcelable

data class JobModel(
    var jobId: String = "",
    val employerId: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val salary: String = "",
    val requirements: String = "",
    val postedDate: Long = System.currentTimeMillis()
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jobId)
        parcel.writeString(employerId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(location)
        parcel.writeString(salary)
        parcel.writeString(requirements)
        parcel.writeLong(postedDate)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<JobModel> {
        override fun createFromParcel(parcel: Parcel): JobModel = JobModel(parcel)
        override fun newArray(size: Int): Array<JobModel?> = arrayOfNulls(size)
    }
}