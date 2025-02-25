// ApplicationRepository.kt
package com.example.rojgarhub.repository

import android.net.Uri
import com.example.rojgarhub.model.ApplicationModel

interface ApplicationRepository {
    fun submitApplication(application: ApplicationModel, callback: (Boolean, String) -> Unit)
    fun updateApplicationStatus(
        applicationId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    )

    fun getApplicationsByUser(
        userId: String,
        callback: (List<ApplicationModel>, Boolean, String) -> Unit
    )

    fun getApplicationsByJob(
        jobId: String,
        callback: (List<ApplicationModel>, Boolean, String) -> Unit
    )

}