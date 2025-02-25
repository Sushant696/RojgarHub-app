package com.example.rojgarhub.repository

import android.util.Log
import com.example.rojgarhub.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserRepositoryImpl : UserRepository {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference: DatabaseReference = database.reference.child("users")

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Login successfull")
            } else {
                callback(false, it.exception?.message.toString())

            }
        }
    }

    override fun signup(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Register Success", auth.currentUser?.uid.toString())
            } else {
                callback(false, it.exception?.message.toString(), "")

            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Password Reset link sent to $email")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun addUserToDatabase(
        userId: String,
        userModel: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).setValue(userModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registered successfully")
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }

    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getUserFromDatabase(
        userId: String,
        callback: (UserModel?, Boolean, String) -> Unit
    ) {
        // Use addListenerForSingleValueEvent instead of addValueEventListener
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FIREBASE", "Snapshot: ${snapshot.value}")
                if (snapshot.exists()) {
                    val model = snapshot.getValue(UserModel::class.java)
                    Log.d("FIREBASE", "Parsed model: $model")
                    callback(model, true, "Success")
                } else {
                    callback(null, false, "User data not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, "Error: ${error.message}")
            }
        })
    }


    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Signout successfull")
        } catch (e: Exception) {
            callback(false, e.message.toString())
        }
    }

    override fun editProfile(
        userId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).updateChildren(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Profile edited successfully")
                } else {
                    callback(false, "Unable to edited profile")

                }
            }
    }
}