package com.example.torontorenthomecompose.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterScreen(
    //onApplyFilters: (priceRange: IntRange, bedrooms: Int, bathrooms: Int, propertyType: String) -> Unit,
    onBackClick: () -> Unit
) {
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf(1) }
    var bathrooms by remember { mutableStateOf(1) }
    var propertyType by remember { mutableStateOf("Apartment") }

    val propertyTypes = listOf("Apartment", "House", "Townhouse", "Condo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            //.windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Close",
                fontSize = 14.sp,
                color = Color.Blue,
                modifier = Modifier.clickable { onBackClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price Range
        Text(text = "Price Range", fontSize = 16.sp)
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = { minPrice = it },
                label = { Text("Min Price") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                label = { Text("Max Price") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bedrooms
        Text(text = "Bedrooms", fontSize = 16.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$bedrooms", fontSize = 14.sp)
            Slider(
                value = bedrooms.toFloat(),
                onValueChange = { bedrooms = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bathrooms
        Text(text = "Bathrooms", fontSize = 16.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$bathrooms", fontSize = 14.sp)
            Slider(
                value = bathrooms.toFloat(),
                onValueChange = { bathrooms = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Property Type
        Text(text = "Property Type", fontSize = 16.sp)
        propertyTypes.forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { propertyType = type }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = propertyType == type,
                    onClick = { propertyType = type }
                )
                Text(text = type, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Button
        Button(
            onClick = {
                val min = minPrice.toIntOrNull() ?: 0
                val max = maxPrice.toIntOrNull() ?: Int.MAX_VALUE
               // onApplyFilters(min..max, bedrooms, bathrooms, propertyType)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Apply Filters")
        }
    }
}
