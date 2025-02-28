package com.example.rojgarhub.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.rojgarhub.viewModel.JobViewModel
import com.example.rojgarhub.viewModel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class Jobs : Fragment() {
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var jobViewModel: JobViewModel
    private lateinit var jobsAdapter: JobsAdapter
    private var currentUser: UserModel? = null

    private val addJobLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Job was added successfully, reload jobs
            currentUser?.let { loadJobs(it) }

            // Check if we need to explicitly stay on Jobs
            val stayOnJobs = result.data?.getBooleanExtra("stayOnJobs", false) ?: false
            if (stayOnJobs) {
                requireActivity().findViewById<BottomNavigationView>(R.id.buttomNavigation)?.selectedItemId = R.id.menuJobs
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        setupViewModels()
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        observeUserAndLoadJobs()
    }

    private fun setupObservers() {
        jobViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            if (_binding != null) {
                jobsAdapter.submitList(jobs)
                binding.jobsRecyclerView.visibility = View.VISIBLE
                binding.progressBarJobs?.visibility = View.GONE
            }
        }

        jobViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (_binding != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                binding.progressBarJobs?.visibility = View.GONE
            }
        }
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

    private fun setupClickListeners() {
        binding.fabAddJob.setOnClickListener {
            // Launch AddJobActivity using the activity result launcher
            addJobLauncher.launch(Intent(requireContext(), AddJobActivity::class.java))
        }
    }

    private fun onJobClicked(job: JobModel) {
        Intent(requireContext(), JobDetailsActivity::class.java).apply {
            putExtra("jobId", job.jobId)
            startActivity(this)
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
        currentUser?.let { user ->
            userViewModel.fetchUser(user.uid)
            userViewModel.userData.observe(viewLifecycleOwner) { userModel ->
                // Check if binding is still valid before using it
                if (userModel != null && _binding != null) {
                    this.currentUser = userModel
                    jobsAdapter.userRole = userModel.role
                    binding.fabAddJob.visibility =
                        if (userModel.role == "employer") View.VISIBLE else View.GONE
                    loadJobs(userModel)
                }
            }
        }
    }

    private fun loadJobs(user: UserModel) {
        binding.progressBarJobs?.visibility = View.VISIBLE

        if (user.role == "employer") {
            jobViewModel.getJobsByEmployer(user.userId) { jobs, success, message ->
                if (_binding == null) return@getJobsByEmployer

                if (success) {
                    jobViewModel.updateJobsLiveData(jobs as List<JobModel>)
                } else {
                    Toast.makeText(requireContext(), message.toString(), Toast.LENGTH_SHORT).show()
                    binding.progressBarJobs?.visibility = View.GONE
                }
            }
        } else {
            jobViewModel.getAllJobs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}