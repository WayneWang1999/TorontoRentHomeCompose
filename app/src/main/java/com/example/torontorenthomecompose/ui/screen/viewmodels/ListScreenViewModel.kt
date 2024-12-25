package com.example.torontorenthomecompose.ui.screen.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.torontorenthome.models.House
import com.example.torontorenthomecompose.data.HouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val houseRepository: HouseRepository
) : ViewModel() {
    // Expose the state from the repository
    val houseList: StateFlow<List<House>> = houseRepository.houseList
    val isLoading: StateFlow<Boolean> = houseRepository.isLoading

    init {
        fetchHouses()
    }
    private fun fetchHouses() {
        viewModelScope.launch {
            houseRepository.fetchHouses()
        }
    }
}
