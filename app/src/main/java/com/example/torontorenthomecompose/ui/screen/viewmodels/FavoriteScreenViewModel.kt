package com.example.torontorenthomecompose.ui.screen.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.example.torontorenthomecompose.data.HouseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FavoriteScreenViewModel (
    private val userStateViewModel: UserStateViewModel,

) : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _favoriteHouseIds = userStateViewModel.favoriteHouseIds

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val favoriteHouses: StateFlow<List<House>> = houseList
        .combine(_favoriteHouseIds) { houses, favoriteIds ->
            houses.filter { it.houseId in favoriteIds }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    init {
        fetchHouses()
    }

//    private fun fetchHouses() {
//        viewModelScope.launch {
//            houseRepository.fetchHouses()
//        }
//    }
    private fun fetchHouses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val houses = db.collection("houses")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(House::class.java) }
                _houseList.value = houses
            } catch (e: Exception) {
                e.printStackTrace()
                _houseList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

