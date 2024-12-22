package com.example.torontorenthomecompose.ui.screen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapScreenViewModel(
    userStateViewModel: UserStateViewModel
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // StateFlows for managing UI state
    private val _houseLocations = MutableStateFlow<List<House>>(emptyList())
    val houseLocations: StateFlow<List<House>> get() = _houseLocations.asStateFlow()

    private val _selectedHouse = MutableStateFlow<House?>(null)
    val selectedHouse: StateFlow<House?> get() = _selectedHouse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()

    init {
        fetchHouseLocations()
    }

    fun setSelectedHouse(house: House?) {
        _selectedHouse.value = house
    }

    fun fetchHouseLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documents = firestore.collection("houses").get().await()
                val houses = documents.mapNotNull { document ->
                    val houseId = document.id // Use the Firestore document ID as houseId
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    val address = document.getString("address")?:""
                    val imageUrl = document.get("imageUrl") as? List<String> ?: emptyList()
                    val price = document.getLong("price")?.toInt() ?: 0
                    val bedrooms = document.getLong("bedrooms")?.toInt() ?: 0
                    val bathrooms = document.getLong("bathrooms")?.toInt() ?: 0
                    val area = document.getLong("area")?.toInt() ?: 0
                    val createTime = document.getString("createTime") ?: ""
                    if (latitude != null && longitude != null) {
                        House(
                            houseId = houseId,
                            latitude = latitude,
                            longitude = longitude,
                            address = address,
                            imageUrl = imageUrl,
                            price = price,
                            bedrooms = bedrooms,
                            bathrooms = bathrooms,
                            area = area,
                            createTime = createTime
                        )
                    } else {
                        null
                    }
                }
                _houseLocations.value = houses
                _errorMessage.value = null // Clear error state
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load house locations: ${e.message}"
            }
        }
    }
}

