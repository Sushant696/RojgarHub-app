package com.example.rojgarhub.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rojgarhub.adapter.ApplicationsAdapter
import com.example.rojgarhub.databinding.FragmentJobApplicationsBinding
import com.example.rojgarhub.repository.ApplicationRepositoryImpl
import com.example.rojgarhub.viewModel.ApplicationViewModel

class JobApplicationsFragment : Fragment() {
    private var _binding: FragmentJobApplicationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var applicationViewModel: ApplicationViewModel
    private lateinit var applicationsAdapter: ApplicationsAdapter
    private var jobId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { jobId = it.getString("jobId") ?: "" }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
//        setupRecyclerView()
//        loadApplications()
        setupObservers()
    }

    private fun setupViewModels() {
        applicationViewModel = ApplicationViewModel(ApplicationRepositoryImpl())
    }
//
//    private fun setupRecyclerView() {
//        applicationsAdapter = ApplicationsAdapter().apply {
//            setOnStatusUpdateListener { application, newStatus ->
//                applicationViewModel.updateApplicationStatus(
//                    application.applicationId, newStatus
//                )
//            }
//        }
//
//        binding.rvApplications.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = applicationsAdapter
//        }
//    }

//    private fun loadApplications() {
//        if (jobId.isNotEmpty()) {
//            applicationViewModel.getApplicationsByJob(jobId) { applications, appSuccess, appMessage ->
//                binding.progressBar.visibility = View.GONE
//
//                if (appSuccess) {
//                    updateApplicationsList(applications)
//                } else {
//                    Toast.makeText(requireContext(), appMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun setupObservers() {
        applicationViewModel.applications.observe(viewLifecycleOwner) { applications ->
            if (applications.isEmpty()) {
                binding.rvApplications.visibility = View.GONE
                binding.tvNoApplications.visibility = View.VISIBLE
            } else {
                binding.rvApplications.visibility = View.VISIBLE
                binding.tvNoApplications.visibility = View.GONE
                applicationsAdapter.submitList(applications)
            }
        }

        applicationViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message ?: "Unknown error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(jobId: String) = JobApplicationsFragment().apply {
            arguments = Bundle().apply { putString("jobId", jobId) }
        }
    }
}