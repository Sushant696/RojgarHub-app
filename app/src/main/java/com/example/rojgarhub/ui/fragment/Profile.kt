package com.example.rojgarhub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rojgarhub.databinding.FragmentProfileBinding
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.ui.activity.EditProfileActivity
import com.example.rojgarhub.ui.activity.LoginActivity
import com.example.rojgarhub.utils.LoadingUtils
import com.example.rojgarhub.viewModel.UserViewModel

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel
    private var currentUserModel: UserModel? = null

    private val TAG = "ProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Initialize ViewModel
            val repo = UserRepositoryImpl()
            userViewModel = UserViewModel(repo)

            // Setup observers
            setupObservers()

            // Setup click listeners
            setupClickListeners()

            // Load user data
            loadUserData()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Error initializing profile: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        // Observe user data
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                updateUI(it)
                currentUserModel = it
            }
        }


    }
    private fun setupClickListeners() {
        // Edit profile card
        binding.editProfile.setOnClickListener {
            navigateToEditProfile()
        }

        // Logout card
        binding.logout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserData() {
        try {
            val currentUser = userViewModel.getCurrentUser()
            Log.d(TAG, "Current user: ${currentUser?.uid}")

            if (currentUser != null) {
                // Let the loading observer handle showing/dismissing
                userViewModel.getUserFromDatabase(currentUser.uid)
            } else {
                Log.d(TAG, "No current user, navigating to login")
                navigateToLogin()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadUserData: ${e.message}", e)
            Toast.makeText(requireContext(), "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(user: UserModel) {
        Log.d("PROFILE", "Updating UI with: $user")
        binding.profileName.text = user.firstName ?: "N/A"
        binding.profileEmail.text = user.email ?: "N/A"
        binding.profileRole.text = user.role ?: "N/A"
    }

    private fun navigateToEditProfile() {
        currentUserModel?.let { user ->
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("USER_DATA", user)
            startActivity(intent)
        }
    }

    private fun logout() {
        try {
            userViewModel.logout { success, message ->
                try {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        navigateToLogin()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in logout callback: ${e.message}", e)

                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in logout: ${e.message}", e)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        try {
            // Refresh user data when coming back to this fragment
            loadUserData()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume: ${e.message}", e)
        }
    }

    override fun onPause() {
        super.onPause()
        // Make sure loading is dismissed when fragment is paused
        try {
        } catch (e: Exception) {
            Log.e(TAG, "Error dismissing loading in onPause: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Make sure loading is dismissed when fragment is destroyed
        try {
        } catch (e: Exception) {
            Log.e(TAG, "Error dismissing loading in onDestroy: ${e.message}", e)
        }
    }
}