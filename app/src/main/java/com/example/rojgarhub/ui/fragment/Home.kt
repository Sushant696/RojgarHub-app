package com.example.rojgarhub.ui.fragment

import JobViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rojgarhub.adapter.ApplicationsAdapter
import com.example.rojgarhub.databinding.FragmentHomeBinding
import com.example.rojgarhub.model.ApplicationModel
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.ApplicationRepositoryImpl
import com.example.rojgarhub.repository.JobRepositoryImpl
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.viewModel.ApplicationViewModel
import com.example.rojgarhub.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var applicationViewModel: ApplicationViewModel
    private lateinit var jobViewModel: JobViewModel
    private lateinit var applicationsAdapter: ApplicationsAdapter

    private var currentUser: UserModel? = null
    private val currentUserId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        setupObservers()
        setupUI()
        checkCurrentUser()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        applicationViewModel = ApplicationViewModel(ApplicationRepositoryImpl())
        jobViewModel = JobViewModel(JobRepositoryImpl())
    }

    private fun setupUI() {
        applicationsAdapter = ApplicationsAdapter(isEmployer = false) { application, newStatus ->
            applicationViewModel.updateApplicationStatus(
                application.applicationId,
                newStatus
            ) { success, message ->
                if (success) loadData()
            }
        }

        binding.rvRecentApplications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applicationsAdapter
        }
    }

    private fun checkCurrentUser() {
        if (currentUserId.isEmpty()) {
            showNotLoggedInUI()
            return
        }

        userViewModel.getUserFromDatabase(currentUserId) { user, success, message ->
            if (success && user != null) {
                currentUser = user
                updateUIBasedOnUserType(user)
                loadData()
            } else {
                Toast.makeText(requireContext(), message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNotLoggedInUI() {
        binding.apply {
            userTypeSection.visibility = View.GONE
            notLoggedInMessage.visibility = View.VISIBLE
            rvRecentApplications.visibility = View.GONE
            applicationsSectionTitle.visibility = View.GONE
            btnViewAll.visibility = View.GONE
        }
    }

    private fun updateUIBasedOnUserType(user: UserModel) {
        binding.apply {
            tvWelcomeMessage.text = "Welcome, ${user.firstName}!"
            applicationsSectionTitle.text = when (user.role) {
                "employer" -> "Recent Applications"
                else -> "Your Applications"
            }
            userTypeSection.visibility = View.VISIBLE
            applicationsSectionTitle.visibility = View.VISIBLE
            btnViewAll.visibility = View.VISIBLE
        }

        applicationsAdapter =
            ApplicationsAdapter(user.role == "employer") { application, newStatus ->
                applicationViewModel.updateApplicationStatus(
                    application.applicationId,
                    newStatus
                ) { success, message ->
                    if (success) loadData()
                }
            }
        binding.rvRecentApplications.adapter = applicationsAdapter
    }

    private fun loadData() {
        currentUser?.let { user ->
            binding.progressBar.visibility = View.VISIBLE
            when (user.role) {
                "employer" -> loadEmployerApplications(user.userId)
                else -> loadJobSeekerApplications(user.userId)
            }
        }
    }

    private fun loadJobSeekerApplications(userId: String) {
        binding.progressBar.visibility = View.VISIBLE

        applicationViewModel.getApplicationsByUser(userId)
    }


    private fun loadEmployerApplications(employerId: String) {
        binding.progressBar.visibility = View.VISIBLE
        applicationViewModel.getApplicationsByUser(employerId)

        jobViewModel.getJobsByEmployer(employerId) { jobs, success, message ->
            binding.progressBar.visibility = View.GONE

            if (success as Boolean) {
                // Explicitly cast jobs to List<JobModel> to help type inference
                val typedJobs = jobs as List<JobModel>

                if (typedJobs.isEmpty()) {
                    updateApplicationsList(emptyList())
                    return@getJobsByEmployer
                }

                val allApplications = mutableListOf<ApplicationModel>()
                var loadedJobCount = 0

                typedJobs.forEach { job ->
                    applicationViewModel.getApplicationsByJob(job.jobId) { applications, appSuccess, _ ->
                        loadedJobCount++

                        if (appSuccess as Boolean) {
                            // Explicitly cast applications to help type inference
                            val typedApplications = applications as List<ApplicationModel>
                            allApplications.addAll(typedApplications)
                        }

                        // Check if this is the last job being processed
                        if (loadedJobCount == typedJobs.size) {
                            updateApplicationsList(allApplications.sortedByDescending { it.applicationDate })
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateApplicationsList(applications: List<ApplicationModel>) {
        if (applications.isEmpty()) {
            showEmptyState()
        } else {
            applicationsAdapter.submitList(applications.take(3))
            binding.rvRecentApplications.visibility = View.VISIBLE
            binding.tvNoApplications.visibility = View.GONE
        }
    }

    private fun showEmptyState() {
        binding.rvRecentApplications.visibility = View.GONE
        binding.tvNoApplications.visibility = View.VISIBLE
        binding.tvNoApplications.text = when (currentUser?.role) {
            "employer" -> "No recent applications"
            else -> "You haven't applied to any jobs yet"
        }
    }

    private fun setupObservers() {
        applicationViewModel.applications.observe(viewLifecycleOwner) { applications ->
            binding.progressBar.visibility = View.GONE
            updateApplicationsList(applications)
        }

        applicationViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}