package com.example.rojgarhub.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rojgarhub.R
import com.example.rojgarhub.databinding.ActivityRegisterBinding
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.utils.LoadingUtils
import com.example.rojgarhub.viewModel.UserViewModel

class Register : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var userViewModel: UserViewModel
    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepositoryImpl()

        userViewModel = UserViewModel(userRepository)

        loadingUtils = LoadingUtils(this)

        binding.createAcc.setOnClickListener {
            loadingUtils.show()
            val email: String = binding.registerEmail.text.toString()
            val password: String = binding.registerPassword.text.toString()
            val username: String = binding.registerFname.text.toString()
            val address: String = binding.registerAddress.text.toString()
            val contact: String = binding.registerContact.text.toString()

            // Get selected role
            val role = if (binding.radioJobSeeker.isChecked) "jobseeker" else "employer"

            // Validate fields
            if (email.isBlank() || password.isBlank() || username.isBlank()) {
                loadingUtils.dismiss()
                Toast.makeText(this@Register, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.signup(email, password) { success, message, userId ->
                if (success) {
                    val userModel = UserModel(
                        userId!!,
                        email,
                        username,
                        address,
                        contact,
                        role
                    )
                    addUser(userModel)
                    Toast.makeText(this@Register, message, Toast.LENGTH_LONG).show()
                    loadingUtils.dismiss()
                    val intent = Intent(this@Register, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Close the register activity
                } else {
                    loadingUtils.dismiss()
                    Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.login.setOnClickListener {
            val intent = Intent(this@Register, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the register activity
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun addUser(userModel: UserModel) {
        userViewModel.addUserToDatabase(userModel.userId, userModel) { success, message ->
            if (success) {
                Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()
            }
            loadingUtils.dismiss()
        }
    }
}