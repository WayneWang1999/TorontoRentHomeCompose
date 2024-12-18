package com.example.torontorenthomecompose.ui.screen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListScreenViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchHouses()
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
}