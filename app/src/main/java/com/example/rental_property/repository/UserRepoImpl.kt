package com.example.rental_property.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.rental_property.model.UserModel


class UserRepoImpl : UserRepo {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun register(userModel: UserModel, pass: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(userModel.email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                db.child(uid).setValue(userModel.copy(userId = uid)).addOnCompleteListener {
                    callback(it.isSuccessful, if (it.isSuccessful) "Success" else it.exception?.message)
                }
            } else callback(false, task.exception?.message)
        }
    }

    override fun login(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            callback(it.isSuccessful, it.exception?.message)
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            callback(it.isSuccessful, it.exception?.message)
        }
    }

    override fun getUserData(uid: String, callback: (UserModel?) -> Unit) {
        db.child(uid).get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(UserModel::class.java))
        }.addOnFailureListener { callback(null) }
    }

    override fun updateUser(uid: String, firstName: String, lastName: String, phone: String, callback: (Boolean, String?) -> Unit) {
        val updates = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phone
        )
        db.child(uid).updateChildren(updates).addOnCompleteListener {
            callback(it.isSuccessful, it.exception?.message)
        }
    }


}