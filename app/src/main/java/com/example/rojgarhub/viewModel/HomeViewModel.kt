package com.example.rojgarhub.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rojgarhub.model.ApplicationModel
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.repository.ApplicationRepository
import com.example.rojgarhub.repository.JobRepository

class HomeViewModel(
    private val jobRepository: JobRepository,
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val _applications = MutableLiveData<List<ApplicationModel>>()
    val applications: LiveData<List<ApplicationModel>> = _applications

    private val _jobs = MutableLiveData<List<JobModel>>()
    val jobs: LiveData<List<JobModel>> = _jobs

    fun loadUserSpecificData(userId: String, isEmployer: Boolean) {
        if (isEmployer) {
            loadEmployerData(userId)
        } else {
            loadJobSeekerData(userId)
        }
    }

    private fun loadJobSeekerData(userId: String) {
        applicationRepository.getApplicationsByUser(userId) { applications, success, message ->
            if (success) {
                _applications.postValue(applications)
            }
        }
    }

    private fun loadEmployerData(employerId: String) {
        jobRepository.getJobsByEmployer(employerId) { jobs, success, message ->
            if (success) {
                _jobs.postValue(jobs)
            }
        }
    }
}