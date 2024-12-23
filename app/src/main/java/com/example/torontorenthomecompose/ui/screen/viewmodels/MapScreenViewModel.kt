package com.example.torontorenthomecompose.ui.screen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapScreenViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // StateFlows for managing UI state
    private val _houseLocations = MutableStateFlow<List<House>>(emptyList())
    val houseLocations: StateFlow<List<House>> get() = _houseLocations.asStateFlow()

    private val _selectedHouse = MutableStateFlow<House?>(null)
    val selectedHouse: StateFlow<House?> get() = _selectedHouse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchHouseLocations()
    }

    fun setSelectedHouse(house: House?) {
        _selectedHouse.value = house
    }

    private fun fetchHouseLocations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val houses = db.collection("houses")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(House::class.java) }
                _houseLocations.value = houses
            } catch (e: Exception) {
                e.printStackTrace()
                _houseLocations.value = emptyList()
                _errorMessage.value = null // Clear error state
            } finally {
                _isLoading.value = false
            }
        }
    }

}

