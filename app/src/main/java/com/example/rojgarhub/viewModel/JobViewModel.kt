import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.repository.JobRepository

class JobViewModel(private val repo: JobRepository) {
    private val _jobs = MutableLiveData<List<JobModel>>()
    val jobs: LiveData<List<JobModel>> = _jobs

    private val _currentJob = MutableLiveData<JobModel?>()
    val currentJob: LiveData<JobModel?> = _currentJob

    fun postJob(jobModel: JobModel, callback: (Boolean, String) -> Unit) {
        repo.postJob(jobModel, callback)
    }

    fun getAllJobs() {
        repo.getAllJobs { jobsList, success, message ->
            if (success) {
                _jobs.value = jobsList
            } else {
                _jobs.value = emptyList()
            }
        }
    }

    fun getJobsByEmployer(employerId: String) {
        repo.getJobsByEmployer(employerId) { jobsList, success, message ->
            if (success) {
                _jobs.value = jobsList
            } else {
                _jobs.value = emptyList()
            }
        }
    }

    fun getJobById(jobId: String) {
        repo.getJobById(jobId) { job, success, message ->
            _currentJob.value = job
        }
    }

    fun updateJob(jobId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateJob(jobId, data, callback)
    }

    fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteJob(jobId, callback)
    }
}