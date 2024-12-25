package com.example.torontorenthomecompose.ui.screen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.example.torontorenthomecompose.data.HouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor (
    private val houseRepository: HouseRepository
): ViewModel() {
    private val _selectedHouse = MutableStateFlow<House?>(null)
    val selectedHouse: StateFlow<House?> get() = _selectedHouse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()
    // Expose the state from the repository
    val houseLocations: StateFlow<List<House>> = houseRepository.houseList
    val isLoading: StateFlow<Boolean> = houseRepository.isLoading

    init {
        fetchHouseLocations()
    }

    fun setSelectedHouse(house: House?) {
        _selectedHouse.value = house
    }

    private fun fetchHouseLocations() {
        viewModelScope.launch {
            houseRepository.fetchHouses()
        }
    }

}

