package com.example.rojgarhub.ui.fragment

import JobViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rojgarhub.R
import com.example.rojgarhub.adapter.JobsAdapter
import com.example.rojgarhub.databinding.FragmentJobsBinding
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.JobRepositoryImpl
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.ui.activity.AddJobActivity
import com.example.rojgarhub.ui.activity.JobApplicationActivity
import com.example.rojgarhub.ui.activity.JobDetailsActivity
import com.example.rojgarhub.viewModel.UserViewModel

class Jobs : Fragment() {
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var jobViewModel: JobViewModel
    private lateinit var jobsAdapter: JobsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModels()
        setupRecyclerView()
        observeUserAndLoadJobs()
        setupClickListeners()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        jobViewModel = JobViewModel(JobRepositoryImpl())
    }

    private fun setupRecyclerView() {
        jobsAdapter = JobsAdapter(
            onJobClicked = { job -> onJobClicked(job) },
            onApplyClicked = { job -> startApplicationProcess(job) }
        )

        binding.jobsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jobsAdapter
        }
    }

    private fun startApplicationProcess(job: JobModel) {
        val intent = Intent(requireContext(), JobApplicationActivity::class.java).apply {
            putExtra("jobId", job.jobId)
            putExtra("jobTitle", job.title)
        }
        startActivity(intent)
    }

    private fun setupClickListeners() {
        binding.fabAddJob.setOnClickListener {
            startActivity(Intent(requireContext(), AddJobActivity::class.java))
        }
    }

    private fun onJobClicked(job: JobModel) {
        Intent(requireContext(), JobDetailsActivity::class.java).apply {
            putExtra("jobId", job.jobId)
            startActivity(this)
        }
    }

    private fun onApplyClicked(job: JobModel) {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            jobViewModel.applyForJob(job.jobId, currentUser.uid) { success, message ->
                if (success as Boolean) {
                    Toast.makeText(
                        requireContext(),
                        "Successfully applied for ${job.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Application failed: $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun observeUserAndLoadJobs() {
        val currentUser = userViewModel.getCurrentUser()

        if (currentUser != null) {
            binding.progressBarJobs.visibility = View.VISIBLE // Add a progress bar to your layout if not already there

            userViewModel.getUserFromDatabase(currentUser.uid) { user, success, message ->
                binding.progressBarJobs.visibility = View.GONE

                if (success && user is UserModel) {
                    // Update adapter with user role
                    jobsAdapter.userRole = user.role
                    binding.fabAddJob.visibility = if (user.role == "employer") View.VISIBLE else View.GONE

                    if (user.role == "employer") {
                        // Fix the TODO() issue by implementing the callback properly
                        jobViewModel.getJobsByEmployer(user.userId) { jobs, success, message ->
                            if (success) {
                                // Handle success case - this will be handled by the LiveData observer
                            } else {
                                Toast.makeText(requireContext(), message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        jobViewModel.getAllJobs()
                    }
                } else {
                    binding.tvNoJobs.visibility = View.VISIBLE
                    binding.tvNoJobs.text = "Error loading user data: $message"
                    binding.jobsRecyclerView.visibility = View.GONE
                }
            }
        } else {
            // Handle not logged in state
            binding.tvNoJobs.visibility = View.VISIBLE
            binding.tvNoJobs.text = "Please login to view jobs"
            binding.jobsRecyclerView.visibility = View.GONE
            binding.fabAddJob.visibility = View.GONE
        }

        jobViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            binding.progressBarJobs.visibility = View.GONE

            if (jobs.isEmpty()) {
                binding.jobsRecyclerView.visibility = View.GONE
                binding.tvNoJobs.visibility = View.VISIBLE

                // Only set default "No jobs" message if there isn't already a specific message
                if (binding.tvNoJobs.text.isNullOrEmpty() ||
                    binding.tvNoJobs.text == getString(R.string.no_jobs_available)) {
                    binding.tvNoJobs.text = "No jobs available"
                }
            } else {
                binding.jobsRecyclerView.visibility = View.VISIBLE
                binding.tvNoJobs.visibility = View.GONE
                jobsAdapter.submitList(jobs)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}