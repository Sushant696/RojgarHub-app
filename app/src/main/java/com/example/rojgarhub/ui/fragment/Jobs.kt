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

    private fun observeUserAndLoadJobs() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            userViewModel.getUserFromDatabase(currentUser.uid) { user, success, message ->
                if (success && user is UserModel) {
                    // Update adapter with user role
                    jobsAdapter.userRole = user.role

                    binding.fabAddJob.visibility = if (user.role == "employer") View.VISIBLE else View.GONE

                    if (user.role == "employer") {
                        jobViewModel.getJobsByEmployer(user.userId)
                    } else {
                        jobViewModel.getAllJobs()
                    }
                } else {
                    binding.tvNoJobs.text = "Error loading user data: $message"
                }
            }
        }

        jobViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            if (jobs.isEmpty()) {
                binding.jobsRecyclerView.visibility = View.GONE
                binding.tvNoJobs.visibility = View.VISIBLE
            } else {
                binding.jobsRecyclerView.visibility = View.VISIBLE
                binding.tvNoJobs.visibility = View.GONE
                jobsAdapter.submitList(jobs)
            }
        }
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
            // Implement your application submission logic here
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}