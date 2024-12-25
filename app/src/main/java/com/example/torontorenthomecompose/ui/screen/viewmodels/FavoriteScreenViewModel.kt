package com.example.torontorenthomecompose.ui.screen.viewmodels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.example.torontorenthomecompose.data.HouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteScreenViewModel @Inject constructor(
    private val houseRepository: HouseRepository
) : ViewModel() {
    val houseList: StateFlow<List<House>> = houseRepository.houseList
    val isLoading: StateFlow<Boolean> = houseRepository.isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    // Accept `favoriteHouseIds` from UserStateViewModel dynamically
    fun getFavoriteHouses(userStateViewModel: UserStateViewModel): StateFlow<List<House>> {
        return houseList
            .combine(userStateViewModel.favoriteHouseIds) { houses, favoriteIds ->
                houses.filter { it.houseId in favoriteIds }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    init {
        fetchHouses()
    }

    private fun fetchHouses() {
        viewModelScope.launch {
            houseRepository.fetchHouses()
        }
    }
}


