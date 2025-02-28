package com.example.rojgarhub.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.repository.JobRepository

class JobViewModel(private val repo: JobRepository) {

    private val _jobs = MutableLiveData<List<JobModel>>()
    val jobs: LiveData<List<JobModel>> get() = _jobs
    private val _currentJob = MutableLiveData<JobModel?>()
    val currentJob: LiveData<JobModel?> = _currentJob

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun updateJobsLiveData(jobsList: List<JobModel>) {
        _jobs.postValue(jobsList)
    }

    fun getAllJobs() {
        repo.getAllJobs { jobs, success, message ->
            if (success) {
                _jobs.postValue(jobs as List<JobModel>

                )
            } else {
                _errorMessage.postValue(message.toString())
            }
        }
    }

    fun getJobsByEmployer(employerId: String, callback: (Any?, Boolean, String?) -> Unit) {
        repo.getJobsByEmployer(employerId, callback)
    }

    fun postJob(jobModel: JobModel, callback: (Boolean, String?) -> Unit) {
        repo.postJob(jobModel, callback)
    }

    fun getJobById(
        jobId: String,
        callback: (JobModel?, Boolean, String) -> Unit
    ) {
        repo.getJobById(jobId) { job, success, message ->
            _currentJob.value = job
            callback(job, success, message)
        }
    }
    fun updateJob(jobId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateJob(jobId, data, callback)
    }

    fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteJob(jobId, callback)
    }
}