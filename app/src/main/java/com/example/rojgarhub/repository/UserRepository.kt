package com.example.rojgarhub.repository

import com.example.rojgarhub.model.UserModel
import com.google.firebase.auth.FirebaseUser


    interface UserRepository {
        // response
//    {
//        "success": true
//        "message":"login success"
//    }

        fun login(email: String, password: String, callback: (Boolean, String) -> Unit)

        fun signup(email: String, password: String, callback: (Boolean, String, String) -> Unit)

        fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)

        fun addUserToDatabase(
            userId: String, userModel: UserModel,
            callback: (Boolean, String) -> Unit
        )

        fun getCurrentUser(): FirebaseUser?

        fun getUserFromDatabase(userId: String, callback: (UserModel?, Boolean, String) -> Unit)
        fun logout(callback: (Boolean, String) -> Unit)
        fun editProfile(
            userId: String,
            data: MutableMap<String, Any>,
            callback: (Boolean, String) -> Unit
        )

    }




