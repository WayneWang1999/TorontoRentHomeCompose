package com.example.torontorenthomecompose.data

import android.util.Log
import com.example.torontorenthome.data.HouseDao
import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HouseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val houseDao: HouseDao
) {

    private val _houseList = MutableStateFlow<List<House>>(emptyList())
    val houseList: StateFlow<List<House>> = _houseList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var housesListener: ListenerRegistration? = null

    // Accept a CoroutineScope as a parameter
    fun startListeningForHouses(coroutineScope: CoroutineScope) {
        _isLoading.value = true

        housesListener = firestore.collection("houses")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("HouseRepository", "Listen failed: ${exception.message}", exception)
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val houseList = snapshot.documents.mapNotNull { it.toObject(House::class.java) }
                    _houseList.value = houseList

                    // Use the provided CoroutineScope
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            houseDao.insertHouses(houseList)
                        } catch (e: Exception) {
                            Log.e("HouseRepository", "Error inserting houses into Room: ${e.message}", e)
                        }
                    }
                }

                _isLoading.value = false
            }
    }

    fun stopListeningForHouses() {
        housesListener?.remove()
        housesListener = null
    }
}

