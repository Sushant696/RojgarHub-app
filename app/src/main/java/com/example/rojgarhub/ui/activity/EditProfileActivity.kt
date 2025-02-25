package com.example.rojgarhub.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.rojgarhub.databinding.ActivityEditProfileBinding
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.utils.LoadingUtils
import com.example.rojgarhub.viewModel.UserViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils
    private var currentUser: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        loadingUtils = LoadingUtils(this)

        // Get current user data from intent
        currentUser = intent.getParcelableExtra("USER_DATA")

        if (currentUser == null) {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Populate fields with current data
        populateFields()

        // Set up button listeners
        binding.btnSave.setOnClickListener {
            saveChanges()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun populateFields() {
        currentUser?.let { user ->
            binding.editUsername.setText(user.firstName)
            binding.editAddress.setText(user.address)
            binding.editPhone.setText(user.phoneNumber)

            // Set role radio button
            if (user.role == "employer") {
                binding.radioEmployer.isChecked = true
            } else {
                binding.radioJobSeeker.isChecked = true
            }
        }
    }

    private fun saveChanges() {
        val username = binding.editUsername.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()
        val role = if (binding.radioJobSeeker.isChecked) "jobseeker" else "employer"

        // Validate input
        if (username.isEmpty()) {
            binding.editUsername.error = "Username is required"
            return
        }

        if (phone.isEmpty()) {
            binding.editPhone.error = "Phone number is required"
            return
        }

        // Create map with changes
        val updates = mutableMapOf<String, Any>()
        updates["firstName"] = username
        updates["address"] = address
        updates["phoneNumber"] = phone
        updates["role"] = role

        loadingUtils.show()
        currentUser?.userId?.let { userId ->
            userViewModel.editProfile(userId, updates) { success, message ->
                loadingUtils.dismiss()
                if (success) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}