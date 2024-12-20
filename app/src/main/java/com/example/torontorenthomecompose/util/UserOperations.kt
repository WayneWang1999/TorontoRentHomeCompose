package com.example.torontorenthomecompose.util

import com.example.torontorenthome.models.Buyer
import com.example.torontorenthome.models.Seller
import com.google.firebase.firestore.FirebaseFirestore

//class UserOperations {
//    private val db = FirebaseFirestore.getInstance()
//
//    fun generateAllUsersAndUpload(){
//        generateTenantsAndUpload()
//        generateOwnersAndUpload()
//    }
//
//    private fun generateOwnersAndUpload() {
//        val batch = db.batch() // Use batch writes for efficiency
//        val ownersCollection = db.collection("owners")
//
//        for (i in 10..20) {
//             // Generate random data
//            val seller = Seller(
//                firstName="owner$i",
//                email="owner$i@gmail.com",
//                password="111111",
//                sellerHouseIds =listOf()
//               )
//
//            // Add to the batch
//            val ownerRef = ownersCollection.document()
//            batch.set(ownerRef, seller)
//        }
//
//        // Commit the batch
//        batch.commit()
//            .addOnSuccessListener {
//                println("Successfully added 50 houses.")
//            }
//            .addOnFailureListener { e ->
//                println("Error adding houses: ${e.message}")
//            }
//    }
//
//    private fun generateTenantsAndUpload() {
//        val batch = db.batch() // Use batch writes for efficiency
//        val ownersCollection = db.collection("tenants")
//
//        for (i in 10..20) {
//            // Generate random data
//            val buyer = Buyer(
//                name="tenant$i",
//                email="tenant$i@gmail.com",
//                password="111111",
//                favoriteHouseIds = listOf(),
//            )
//
//            // Add to the batch
//            val tenantRef = ownersCollection.document()
//            batch.set(tenantRef, buyer)
//        }
//
//        // Commit the batch
//        batch.commit()
//            .addOnSuccessListener {
//                println("Successfully added 50 houses.")
//            }
//            .addOnFailureListener { e ->
//                println("Error adding houses: ${e.message}")
//            }
//    }
//}