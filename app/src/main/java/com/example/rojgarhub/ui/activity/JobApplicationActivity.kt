package com.example.rojgarhub.ui.activity

import com.example.rojgarhub.viewModel.JobViewModel
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rojgarhub.databinding.ActivityJobApplicationBinding
import com.example.rojgarhub.model.ApplicationModel
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.ApplicationRepositoryImpl
import com.example.rojgarhub.repository.JobRepositoryImpl
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.viewModel.ApplicationViewModel
import com.example.rojgarhub.viewModel.UserViewModel

class JobApplicationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobApplicationBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var jobViewModel: JobViewModel
    private lateinit var applicationViewModel: ApplicationViewModel

    private var jobId: String = ""
    private var jobTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        jobId = intent.getStringExtra("jobId") ?: ""
        if (jobId.isEmpty()) {
            Toast.makeText(this, "Error: Job ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViewModels()
        loadJobDetails()
        loadUserDetails()
        setupListeners()
        observeData()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        jobViewModel = JobViewModel(JobRepositoryImpl())
        applicationViewModel = ApplicationViewModel(ApplicationRepositoryImpl())
    }

    private fun loadJobDetails() {
        jobViewModel.getJobById(jobId) { job, success, message ->
            if (success && job != null) {
                jobTitle = job.title
                binding.tvJobTitle.text = job.title
                binding.tvJobCompany.text = job.companyName
            } else {
                Toast.makeText(this, "Error loading job details: $message", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadUserDetails() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            userViewModel.getUserFromDatabase(currentUser.uid) { user, success, message ->
                if (success && user is UserModel) {
                    binding.etApplicantName.setText(user.firstName)
                    binding.etApplicantEmail.setText(user.email)
                    binding.etApplicantPhone.setText(user.phoneNumber)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSubmitApplication.setOnClickListener {
            if (validateInputs()) {
                submitApplication()
            }
        }
    }

    private fun observeData() {
        applicationViewModel.applicationStatus.observe(this) { resource ->
            when (resource) {
                is ApplicationViewModel.Resource.Loading -> {
                    binding.btnSubmitApplication.isEnabled = false
                    binding.btnSubmitApplication.text = "Submitting..."
                }
                is ApplicationViewModel.Resource.Success -> {
                    binding.btnSubmitApplication.isEnabled = true
                    binding.btnSubmitApplication.text = "Submit Application"
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is ApplicationViewModel.Resource.Error -> {
                    binding.btnSubmitApplication.isEnabled = true
                    binding.btnSubmitApplication.text = "Submit Application"
                    Toast.makeText(this, "Submission failed: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.etApplicantName.text.toString().trim()
        val email = binding.etApplicantEmail.text.toString().trim()
        val phone = binding.etApplicantPhone.text.toString().trim()
        val coverLetter = binding.etCoverLetter.text.toString().trim()

        if (name.isEmpty()) binding.etApplicantName.error = "Name required"
        if (email.isEmpty()) binding.etApplicantEmail.error = "Email required"
        if (phone.isEmpty()) binding.etApplicantPhone.error = "Phone required"
        if (coverLetter.isEmpty()) binding.etCoverLetter.error = "Cover letter required"

        return listOf(name, email, phone, coverLetter).all { it.isNotEmpty() }
    }

    private fun submitApplication() {
        val currentUser = userViewModel.getCurrentUser()
        Log.d("JobApp", "Starting application submission...")
        if (currentUser != null) {
            val application = ApplicationModel(
                jobId = jobId,
                userId = currentUser.uid,
                jobTitle = jobTitle,
                applicantName = binding.etApplicantName.text.toString().trim(),
                applicantEmail = binding.etApplicantEmail.text.toString().trim(),
                applicantPhone = binding.etApplicantPhone.text.toString().trim(),
                applicationDate = System.currentTimeMillis(),
                status = "pending"
            )
            Log.d("JobApp", "Application model created: $application")
            applicationViewModel.submitApplication(application)
        }
    }
}