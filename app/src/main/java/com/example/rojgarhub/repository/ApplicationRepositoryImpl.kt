package com.example.rojgarhub.repository

import com.example.rojgarhub.model.ApplicationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplicationRepositoryImpl : ApplicationRepository {
    // Use only Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private val applicationsRef = database.reference.child("applications")

    override fun submitApplication(application: ApplicationModel, callback: (Boolean, String) -> Unit) {
        // Generate a unique ID for the application if not provided
        val applicationId = if (application.applicationId.isEmpty())
            applicationsRef.push().key ?: return
        else
            application.applicationId

        // Create a new application with the generated ID
        val updatedApplication = application.copy(applicationId = applicationId)

        // Save to Realtime Database
        applicationsRef.child(applicationId).setValue(updatedApplication)
            .addOnSuccessListener {
                callback(true, "Application submitted successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to submit application")
            }
    }

    override fun updateApplicationStatus(
        applicationId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        applicationsRef.child(applicationId).child("status").setValue(status)
            .addOnSuccessListener {
                callback(true, "Application status updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update application status")
            }
    }

    override fun getApplicationsByUser(
        userId: String,
        callback: (List<ApplicationModel>, Boolean, String) -> Unit
    ) {
        applicationsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val applicationsList = mutableListOf<ApplicationModel>()
                    for (appSnapshot in snapshot.children) {
                        appSnapshot.getValue(ApplicationModel::class.java)?.let {
                            applicationsList.add(it)
                        }
                    }
                    callback(applicationsList, true, "Applications fetched successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }

    override fun getApplicationsByJob(
        jobId: String,
        callback: (List<ApplicationModel>, Boolean, String) -> Unit
    ) {
        applicationsRef.orderByChild("jobId").equalTo(jobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val applicationsList = mutableListOf<ApplicationModel>()
                    for (appSnapshot in snapshot.children) {
                        appSnapshot.getValue(ApplicationModel::class.java)?.let {
                            applicationsList.add(it)
                        }
                    }
                    callback(applicationsList, true, "Applications fetched successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }
}