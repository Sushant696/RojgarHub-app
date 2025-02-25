package com.example.rojgarhub.repository

import com.example.rojgarhub.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JobRepositoryImpl : JobRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val jobsReference: DatabaseReference = database.reference.child("jobs")
    private val applicationsReference: DatabaseReference = database.reference.child("applications")

    override fun postJob(jobModel: JobModel, callback: (Boolean, String) -> Unit) {
        val jobId = jobsReference.push().key ?: return
        jobModel.jobId = jobId

        jobsReference.child(jobId).setValue(jobModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Job posted successfully")
                } else {
                    callback(false, it.exception?.message ?: "Failed to post job")
                }
            }
    }

    override fun getAllJobs(callback: (List<JobModel>, Boolean, String) -> Unit) {
        jobsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobsList = mutableListOf<JobModel>()
                for (jobSnapshot in snapshot.children) {
                    jobSnapshot.getValue(JobModel::class.java)?.let {
                        jobsList.add(it)
                    }
                }
                callback(jobsList, true, "Jobs fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }

    override fun getJobsByEmployer(employerId: String, callback: (List<JobModel>, Boolean, String) -> Unit) {
        jobsReference.orderByChild("employerId").equalTo(employerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobsList = mutableListOf<JobModel>()
                    for (jobSnapshot in snapshot.children) {
                        jobSnapshot.getValue(JobModel::class.java)?.let {
                            jobsList.add(it)
                        }
                    }
                    callback(jobsList, true, "Jobs fetched successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }

    override fun getJobById(jobId: String, callback: (JobModel?, Boolean, String) -> Unit) {
        jobsReference.child(jobId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val job = task.result?.getValue(JobModel::class.java)
                callback(job, true, "Success")
            } else {
                callback(null, false, task.exception?.message ?: "Error")
            }
        }
    }

    override fun updateJob(jobId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        jobsReference.child(jobId).updateChildren(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Job updated successfully")
                } else {
                    callback(false, it.exception?.message ?: "Failed to update job")
                }
            }
    }

    override fun applyForJob(jobId: String, userId: String, callback: (Boolean, String) -> Unit) {
        // Changed to use Realtime Database instead of Firestore
        val applicationId = applicationsReference.push().key ?: return

        val application = HashMap<String, Any>()
        application["applicationId"] = applicationId
        application["jobId"] = jobId
        application["userId"] = userId
        application["timestamp"] = System.currentTimeMillis()
        application["status"] = "pending"

        applicationsReference.child(applicationId).setValue(application)
            .addOnSuccessListener {
                callback(true, "Application submitted")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Application failed")
            }
    }

    override fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit) {
        jobsReference.child(jobId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Job deleted successfully")
                } else {
                    callback(false, it.exception?.message ?: "Failed to delete job")
                }
            }
    }
}