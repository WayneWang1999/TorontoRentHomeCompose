package com.example.torontorenthomecompose.data

import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HouseRepository @Inject constructor(
    private val firestore: FirebaseFirestore  // Inject FirebaseFirestore
) {

    // MutableStateFlow for managing house list and loading state
    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Fetch houses from Firestore and update the flow states
    suspend fun fetchHouses() {
        _isLoading.value = true  // Start loading
        try {
            // Fetch the list of houses from Firestore
            val houses = firestore.collection("houses")
                .get()
                .await()  // Asynchronously await the result
                .documents
                .mapNotNull { it.toObject(House::class.java) }

            _houseList.value = houses  // Update house list
        } catch (e: Exception) {
            // Handle error, print stack trace, and set empty list in case of failure
            e.printStackTrace()
            _houseList.value = emptyList()
        } finally {
            _isLoading.value = false  // End loading
        }
    }
}