package com.example.rojgarhub.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rojgarhub.model.UserModel
import com.example.rojgarhub.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repository.login(email, password) { success, message ->
            _loading.value = false
            if (success) {
                // If login successful, fetch the user data
                val currentUser = repository.getCurrentUser()
                currentUser?.let {
                    getUserFromDatabase(it.uid)
                }
            }
            callback(success, message)
        }
    }

    fun signup(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        _loading.value = true
        repository.signup(email, password) { success, message, userId ->
            _loading.value = false
            callback(success, message, userId)
        }
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repository.forgetPassword(email) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun addUserToDatabase(
        userId: String,
        userModel: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repository.addUserToDatabase(userId, userModel) { success, message ->
            _loading.value = false
            if (success) {
                _userData.value = userModel
            }
            callback(success, message)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }

    fun getUserFromDatabase(
        userId: String,
        callback: ((UserModel?, Boolean, String) -> Unit)? = null
    ) {
        _loading.value = true
        repository.getUserFromDatabase(userId) { userModel, success, message ->
            Log.d("VIEWMODEL", "Received user: $userModel, success: $success")
            _loading.value = false // Explicitly set loading to false
            _userData.value = userModel
            // Forward the callback results if a callback was provided
            callback?.invoke(userModel, success, message)
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repository.logout { success, message ->
            _loading.value = false
            if (success) {
                _userData.value = null
            }
            callback(success, message)
        }
    }

    fun editProfile(
        userId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true
        repository.editProfile(userId, data) { success, message ->
            _loading.value = false
            if (success) {
                // Update the local user data with the changes
                val currentUserData = _userData.value
                if (currentUserData != null) {
                    val updatedUser = currentUserData.copy()

                    // Apply each update to the user model
                    data["firstName"]?.let { updatedUser.firstName = it as String }
                    data["address"]?.let { updatedUser.address = it as String }
                    data["phoneNumber"]?.let { updatedUser.phoneNumber = it as String }
                    data["role"]?.let { updatedUser.role = it as String }

                    _userData.value = updatedUser
                }
            }
            callback(success, message)
        }
    }
}