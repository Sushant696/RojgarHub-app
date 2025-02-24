package com.example.rojgarhub.ui.fragment

import JobRepositoryImpl
import JobViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rojgarhub.adapter.JobsAdapter
import com.example.rojgarhub.databinding.FragmentJobsBinding
import com.example.rojgarhub.model.JobModel
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.ui.activity.AddJobActivity
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
        jobsAdapter = JobsAdapter { job -> onJobClicked(job) }
        binding.jobsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jobsAdapter
        }
    }

    private fun observeUserAndLoadJobs() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            userViewModel.getUserFromDatabase(currentUser.uid) { user, success, message ->
                if (success as Boolean && user is UserModel) {
                    binding.fabAddJob.visibility = if (user.role == "employer") View.VISIBLE else View.GONE

                    if (user.role == "employer") {
                        jobViewModel.getJobsByEmployer(user.userId)
                    } else {
                        jobViewModel.getAllJobs()
                    }
                }
            }
        }

        jobViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            println("jobviewmodel.jobs.observer")
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
            val intent = Intent(requireContext(), AddJobActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onJobClicked(job: JobModel) {
        val intent = Intent(requireContext(), JobDetailsActivity::class.java).apply {
            putExtra("jobId", job.jobId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}