package com.example.rental_property.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rental_property.repository.RentRepo
import com.example.rental_property.repository.UserRepo

class ViewModelFactory(private val repository: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(repository as UserRepo) as T
            }
            modelClass.isAssignableFrom(RentViewModel::class.java) -> {
                RentViewModel(repository as RentRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}