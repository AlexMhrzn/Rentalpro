package com.example.rental_property.repository

import android.net.Uri
import com.example.rental_property.model.UserModel

interface UserRepo {
    fun register(userModel: UserModel, pass: String, callback: (Boolean, String?) -> Unit)
    fun login(email: String, pass: String, callback: (Boolean, String?) -> Unit)
    fun forgetPassword(email: String, callback: (Boolean, String?) -> Unit)
    fun getUserData(uid: String, callback: (UserModel?) -> Unit)
    fun updateUser(uid: String, firstName: String, lastName: String, phone: String, callback: (Boolean, String?) -> Unit)

}