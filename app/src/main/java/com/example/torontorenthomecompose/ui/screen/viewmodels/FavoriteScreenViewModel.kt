package com.example.torontorenthomecompose.ui.screen.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavoriteScreenViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _favoriteHouseIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteHouses: StateFlow<List<House>> = _houseList
        .combine(_favoriteHouseIds) { houses, favoriteIds ->
            houses.filter { it.houseId in favoriteIds }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchHouses()
        fetchFavoriteHouseIds()
    }

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

    private fun fetchFavoriteHouseIds() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val document = db.collection("buyers")
                        .document(user.uid)
                        .get()
                        .await()

                    val favoriteHouseIds = document["favoriteHouseIds"] as? List<String> ?: emptyList()
                    _favoriteHouseIds.value = favoriteHouseIds.toSet()
                    Log.d("FavoriteHouseFetch", "Fetched favorite house IDs: ${_favoriteHouseIds.value}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    _favoriteHouseIds.value = emptySet()
                    Log.e("FavoriteHouseFetch", "Error fetching favorite house IDs", e)
                }
            }
        }
    }



    fun toggleFavorite(houseId: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val currentFavorites = _favoriteHouseIds.value.toMutableSet()
                if (currentFavorites.contains(houseId)) {
                    currentFavorites.remove(houseId)
                } else {
                    currentFavorites.add(houseId)
                }

                _favoriteHouseIds.value = currentFavorites

                try {
                    db.collection("buyers")
                        .document(user.uid) // Use UID as the document ID
                        .update("favoriteHouseIds", currentFavorites.toList())
                        .await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun logout() {
        // Clear the user's favorite house IDs
        _favoriteHouseIds.value = emptySet()

        // Optionally, clear the house list if needed
        _houseList.value = emptyList()

        // Perform Firebase Auth logout
        auth.signOut()

        // Reset the isLoading state
        _isLoading.value = true

        Log.d("FavoriteScreenViewModel", "User logged out, favoriteHouseIds cleared.")
    }
}