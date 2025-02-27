package com.example.rojgarhub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rojgarhub.adapter.ApplicationsAdapter
import com.example.rojgarhub.databinding.FragmentMyApplicationsBinding
import com.example.rojgarhub.repository.ApplicationRepositoryImpl
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.viewModel.ApplicationViewModel
import com.example.rojgarhub.viewModel.UserViewModel


class MyApplicationsFragment : Fragment() {
    private var _binding: FragmentMyApplicationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var applicationViewModel: ApplicationViewModel
    private lateinit var applicationsAdapter: ApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModels()
        setupRecyclerView()
        loadApplications()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        applicationViewModel = ApplicationViewModel(ApplicationRepositoryImpl())
    }

    private fun setupRecyclerView() {
        // Add the required parameters to the adapter
        applicationsAdapter = ApplicationsAdapter(
            isEmployer = false,  // Set to appropriate value based on your app's logic
            onStatusUpdate = { application, newStatus ->
                // Implement status update logic here
            }
        )

        binding.rvApplications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = applicationsAdapter
        }
    }


    private fun loadApplications() {
        binding.progressBar.visibility = View.VISIBLE

        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            // Don't pass a lambda here since getApplicationsByUser isn't using it properly
            applicationViewModel.getApplicationsByUser(currentUser.uid)

            // Instead, observe the LiveData that gets updated
            applicationViewModel.applications.observe(viewLifecycleOwner) { applications ->
                binding.progressBar.visibility = View.GONE

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
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), message ?: "Unknown error", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvApplications.visibility = View.GONE
            binding.tvNoApplications.visibility = View.VISIBLE
        }
    }


}