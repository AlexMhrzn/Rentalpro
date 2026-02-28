package com.example.rental_property.model

data class UserModel(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val role: String = "user",
    val profileImage: String = "" // Add this field
)