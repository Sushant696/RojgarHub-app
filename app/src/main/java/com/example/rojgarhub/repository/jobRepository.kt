package com.example.rojgarhub.repository

import com.example.rojgarhub.model.JobModel

interface JobRepository {
    fun postJob(jobModel: JobModel, callback: (Boolean, String) -> Unit)
    fun getAllJobs(callback: (List<JobModel>, Boolean, String) -> Unit)
    fun getJobsByEmployer(employerId: String, callback: (List<JobModel>, Boolean, String) -> Unit)
    fun getJobById(jobId: String, callback: (JobModel?, Boolean, String) -> Unit)
    fun updateJob(jobId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit)
    fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit)
    fun applyForJob(jobId: String, userId: String, callback: (Boolean, String) -> Unit)
}
