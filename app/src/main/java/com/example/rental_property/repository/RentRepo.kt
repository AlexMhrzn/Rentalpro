package com.example.rental_property.repository

import android.net.Uri
import com.example.rental_property.model.RentModel

interface RentRepo {
    fun getAllRents(callback: (List<RentModel>?, String?) -> Unit)
    fun addRent(rentModel: RentModel, callback: (Boolean, String?) -> Unit)
    fun deleteRent(rentId: String, callback: (Boolean, String?) -> Unit)
    fun updateRentStatus(rentId: String, status: String, buyerId: String, callback: (Boolean, String?) -> Unit)
    fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit)
}