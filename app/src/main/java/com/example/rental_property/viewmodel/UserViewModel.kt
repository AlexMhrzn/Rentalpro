package com.example.rental_property.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.rental_property.model.UserModel
import com.example.rental_property.repository.UserRepo

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    // Fetches user data (Name, Role, Profile Image URL) from Realtime Database
    fun getUserData(uid: String, callback: (UserModel?) -> Unit) {
        repo.getUserData(uid, callback)
    }

    // Handles Password Reset logic
    fun forgetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    // Updates text-based profile information (First Name, Last Name, Phone)
    fun updateUser(uid: String, fName: String, lName: String, phone: String, callback: (Boolean, String?) -> Unit) {
        repo.updateUser(uid, fName, lName, phone, callback)
    }

    // Handles the Image Upload process to Firebase Storage and saves the resulting URL

}