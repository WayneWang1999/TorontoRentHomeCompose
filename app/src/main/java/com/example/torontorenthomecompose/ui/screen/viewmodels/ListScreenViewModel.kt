package com.example.torontorenthomecompose.ui.screen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListScreenViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _favoriteHouseIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteHouseIds: StateFlow<Set<String>> = _favoriteHouseIds

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
                    val favoriteIds = document["favoriteHouseIds"] as? List<String> ?: emptyList()
                    _favoriteHouseIds.value = favoriteIds.toSet()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _favoriteHouseIds.value = emptySet()
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
                        .document(user.uid)
                        .update("favoriteHouseIds", currentFavorites.toList())
                        .await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}