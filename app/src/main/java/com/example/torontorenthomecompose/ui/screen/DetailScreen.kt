package com.example.torontorenthomecompose.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.torontorenthome.models.House
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    houseId: String?
) {
    // State to hold the house data
    val house = produceState<House?>(null, houseId) {
        if (houseId != null) {
            value = fetchHouseFromFirestore(houseId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Content container
        when (val houseDetails = house.value) {
            null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image Pager
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically // Aligns the back button and text vertically
                    ) {
                        // Back Button
                        IconButton(
                            onClick = { onBackClick() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",

                                )
                        }

                        // Text
                        Text(
                            text = "This is the House detail",
                            style= MaterialTheme.typography.titleLarge,
                            //fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp) // Add padding between button and text
                        )
                    }

                    HouseImagesPagerWithArrows(
                        imageUrls = houseDetails.imageUrl,

                        )

                    // House Info
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Address:",
                            style=MaterialTheme.typography.titleMedium,
//                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = houseDetails.address,
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Divider
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        val formattedPrice =
                            NumberFormat.getNumberInstance(Locale.US).format(houseDetails.price)
                        // Price
                        Text(
                            text = "Price: $formattedPrice",
                            style=MaterialTheme.typography.titleLarge,
                           // fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Other Details
                        Text(
                            text = "Bedrooms: ${houseDetails.bedrooms}   Bathrooms: ${houseDetails.bathrooms}",
//                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Area: ${houseDetails.area} sqft",
//                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Availability or Custom Tags
                        Text(
                            text = if (houseDetails.isAvailable) "Available" else "Sold Out",
//                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            // color = if (houseDetails.isAvailable) Color(0xFF4CAF50) else Color.Red,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        // Availability or Custom Tags
                        Text(
                            text = "List Description",
                            style = MaterialTheme.typography.titleMedium,
//                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "${houseDetails.description}",
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }

}

suspend fun fetchHouseFromFirestore(houseId: String): House? {
    val db = Firebase.firestore // Initialize Firestore
    return try {
        val document = db.collection("houses").document(houseId).get().await()
        if (document.exists()) {
            document.toObject(House::class.java) // Map Firestore data to the House model
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
