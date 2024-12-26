package com.example.torontorenthomecompose.data

import com.example.torontorenthome.data.HouseDao
import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HouseRepository @Inject constructor(
    private val firestore: FirebaseFirestore ,
    private val houseDao: HouseDao    // Inject FirebaseFirestore
) {

    // MutableStateFlow for managing house list and loading state
    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun fetchHouses() {
        _isLoading.value = true
        try {
            // Preload local data
            val cachedHouses = houseDao.getAllHouses()
            if (cachedHouses.isNotEmpty()) {
                _houseList.value = cachedHouses
            }

            // Fetch from Firestore
            val houses = firestore.collection("houses")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(House::class.java) }

            // Update StateFlow and Room
            _houseList.value = houses
          //  houseDao.clearAll()
            houseDao.insertHouses(houses)
        } catch (e: Exception) {
            e.printStackTrace()
            _houseList.value = houseDao.getAllHouses() // Fallback to local cache
        } finally {
            _isLoading.value = false
        }
    }

}