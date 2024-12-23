package com.example.torontorenthome.data

import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HouseRepository(
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchHouses(): List<House> {
        return try {
            firestore.collection("houses")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(House::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}



