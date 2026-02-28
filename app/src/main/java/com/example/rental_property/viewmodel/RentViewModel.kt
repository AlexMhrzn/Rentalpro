package com.example.rental_property.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rental_property.model.RentModel
import com.example.rental_property.repository.RentRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RentViewModel(val repo: RentRepo) : ViewModel() {
    private val _rents = MutableStateFlow<List<RentModel>>(emptyList())
    val rents: StateFlow<List<RentModel>> = _rents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchRents() {
        _isLoading.value = true
        repo.getAllRents { list, error ->
            _isLoading.value = false
            if (list != null) _rents.value = list
        }
    }
}