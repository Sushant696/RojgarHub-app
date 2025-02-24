package com.example.rojgarhub.ui.activity

import JobRepositoryImpl
import JobViewModel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rojgarhub.R
import com.example.rojgarhub.databinding.ActivityAddJobBinding
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.utils.LoadingUtils
import com.example.rojgarhub.viewModel.UserViewModel

class AddJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddJobBinding
    private lateinit var jobViewModel: JobViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModels()
        setupViews()
    }

    private fun setupViewModels() {
        jobViewModel = JobViewModel(JobRepositoryImpl())
        userViewModel = UserViewModel(UserRepositoryImpl())
        loadingUtils = LoadingUtils(this)
    }

    private fun setupViews() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Post New Job"

        // Setup submit button
        binding.btnSubmitJob.setOnClickListener {
            submitJob()
        }
    }

    private fun submitJob() {
        val title = binding.etJobTitle.text.toString()
        val description = binding.etJobDescription.text.toString()
        val location = binding.etJobLocation.text.toString()
        val salary = binding.etJobSalary.text.toString()
        val requirements = binding.etJobRequirements.text.toString()

        if (validateInputs()) {
            loadingUtils.show()

            val currentUser = userViewModel.getCurrentUser()
            if (currentUser != null) {
                val jobModel = JobModel(
                    employerId = currentUser.uid,
                    title = title,
                    description = description,
                    location = location,
                    salary = salary,
                    requirements = requirements
                )

                jobViewModel.postJob(jobModel) { success, message ->
                    loadingUtils.dismiss()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        finish()
                    }
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Add your validation logic here
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}