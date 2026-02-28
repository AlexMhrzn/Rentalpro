package com.example.rental_property.repository

import android.net.Uri
import com.example.rental_property.model.RentModel
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RentRepoImpl : RentRepo {
    private val db = FirebaseDatabase.getInstance().getReference("rents")
    private val storage = FirebaseStorage.getInstance().getReference("property_images")

    override fun getAllRents(callback: (List<RentModel>?, String?) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(RentModel::class.java) }
                callback(list, null)
            }
            override fun onCancelled(error: DatabaseError) { callback(null, error.message) }
        })
    }

    override fun addRent(rentModel: RentModel, callback: (Boolean, String?) -> Unit) {
        val id = rentModel.rentId.ifEmpty { db.push().key ?: "" }
        db.child(id).setValue(rentModel.copy(rentId = id)).addOnCompleteListener {
            callback(it.isSuccessful, it.exception?.message)
        }
    }

    override fun deleteRent(rentId: String, callback: (Boolean, String?) -> Unit) {
        db.child(rentId).removeValue().addOnCompleteListener { callback(it.isSuccessful, it.exception?.message) }
    }

    override fun updateRentStatus(rentId: String, status: String, buyerId: String, callback: (Boolean, String?) -> Unit) {
        val updates = mapOf("status" to status, "buyerId" to buyerId)
        db.child(rentId).updateChildren(updates).addOnCompleteListener {
            callback(it.isSuccessful, it.exception?.message)
        }
    }

    override fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val imageName = UUID.randomUUID().toString()
        val imageRef = storage.child(imageName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { url ->
                    callback(true, url.toString())
                }.addOnFailureListener {
                    callback(false, it.message)
                }
            }
            .addOnFailureListener {
                callback(false, it.message)
            }
    }
}