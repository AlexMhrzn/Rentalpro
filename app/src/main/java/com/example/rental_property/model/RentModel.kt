package com.example.rental_property.model

data class RentModel(
    val rentId: String = "",
    val title: String = "",
    val price: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val status: String = "available", // available, pending, sold
    val buyerId: String = "",         // UID of the user requesting to buy
    val ownerId: String = ""          // UID of the admin who posted it
)