package com.example.rojgarhub.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rojgarhub.model.ApplicationModel
import com.example.rojgarhub.repository.ApplicationRepository

class ApplicationViewModel(private val repository: ApplicationRepository) : ViewModel() {
    private val _applicationStatus = MutableLiveData<Resource<String>>()
    val applicationStatus: LiveData<Resource<String>> = _applicationStatus

    private val _applications = MutableLiveData<List<ApplicationModel>>()
    val applications: LiveData<List<ApplicationModel>> = _applications

    private val _cvUploadStatus = MutableLiveData<Resource<String?>>()
    val cvUploadStatus: LiveData<Resource<String?>> = _cvUploadStatus

    fun submitApplication(application: ApplicationModel) {
        _applicationStatus.value = Resource.Loading()
        repository.submitApplication(application) { success, message ->
            if (success) {
                _applicationStatus.value = Resource.Success(message)
            } else {
                _applicationStatus.value = Resource.Error(message)
            }
        }
    }


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getApplicationsByJob(jobId: String) {
        repository.getApplicationsByJob(jobId) { applications, success, message ->
            if (success) {
                _applications.value = applications
            } else {
                _applications.value = emptyList()
            }
        }
    }

    fun getApplicationsByUser(userId: String) {
        repository.getApplicationsByUser(userId) { applications, success, message ->
            if (success) {
                _applications.value = applications
            } else {
                _applications.value = emptyList()
            }
        }
    }


    fun updateApplicationStatus(applicationId: String, status: String) {
        _applicationStatus.value = Resource.Loading()
        repository.updateApplicationStatus(applicationId, status) { success, message ->
            if (success) {
                _applicationStatus.value = Resource.Success(message)
            } else {
                _applicationStatus.value = Resource.Error(message)
            }
        }
    }


    // Resource class for handling states
    sealed class Resource<T>(
        val data: T? = null,
        val message: String? = null
    ) {
        class Success<T>(data: T) : Resource<T>(data)
        class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
        class Loading<T> : Resource<T>()
    }
}