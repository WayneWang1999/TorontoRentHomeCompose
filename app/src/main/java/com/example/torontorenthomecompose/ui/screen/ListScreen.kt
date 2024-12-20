package com.example.torontorenthomecompose.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.ListScreenViewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel

@Composable
fun ListScreen(
    userStateViewModel: UserStateViewModel,
    onFilterClick: () -> Unit,
    onItemClick: () -> Unit,
) {
    val listScreenViewModel: ListScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListScreenViewModel(userStateViewModel) as T
            }
        }
    )

    val isLoggedIn by userStateViewModel.isLoggedIn.collectAsState()
    val houseList by listScreenViewModel.houseList.collectAsState()
    val isLoading by listScreenViewModel.isLoading.collectAsState()
    val favoriteHouseIds by userStateViewModel.favoriteHouseIds.collectAsState()

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

// Filters from the UserStateViewModel
    val filters by userStateViewModel.filters.collectAsState()

// Apply both search query and filters
    val filteredHouses = remember(searchQuery, filters,houseList) {
        houseList.filter { house ->
            val matchesSearchQuery = searchQuery.isBlank() || house.address.contains(searchQuery, ignoreCase = true)
            val matchesFilters = filters?.let {
                house.price in it.priceRange &&
                        house.bedrooms >= it.bedrooms &&
                        house.bathrooms >= it.bathrooms
            } ?: true // Show all houses if filters are null

            matchesSearchQuery && matchesFilters
        }.also {
            if (filters == null && searchQuery.isBlank()) {
                Log.d("FilteredHouses", "No filters or search query applied. Showing all houses.")
            } else {
                Log.d("FilteredHouses", "Search query: '$searchQuery', Filters: $filters")
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Component
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Slightly taller for better visibility
                .background(Color(0xFF1E88E5)), // Updated to a more modern blue shade
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo or Title
            Text(
                text = "TORH.ca",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(0.25f),
                color = Color.White,
                fontSize = 22.sp,
                maxLines = 1
            )

            // Search Component
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search...") },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .weight(0.6f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5), // Background color
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Blue, // Border color when focused
                    unfocusedIndicatorColor = Color.Gray, // Border color when not focused
                    focusedLabelColor = Color.Blue, // Label color when focused
                    unfocusedLabelColor = Color.Gray // Label color when not focused
                )
            )

            val badgeCount = if (filters != null) 1 else 0

            FilterIconWithBadge(
                onFilterClick = { onFilterClick() },
                badgeCount = badgeCount
            )
        }


        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Loading...", fontSize = 20.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredHouses.size) { index ->
                    val house = filteredHouses[index]
                    val isFavorite = if (isLoggedIn) {
                        favoriteHouseIds.contains(house.houseId)
                    } else {
                        false
                    }
                    Box(modifier = Modifier.clickable { onItemClick() })
                    {HouseItem(
                        houseId = house.houseId,
                        imageUrl = house.imageUrl,
                        price = house.price,
                        bedrooms = house.bedrooms,
                        address = house.address,
                        bathrooms = house.bathrooms,
                        area = house.area,
                        createTime = house.createTime,
                        onFavoriteClick = { houseId ->
                            userStateViewModel.toggleFavorite(houseId)
                        },
                        isFavorite = isFavorite
                    )

                    }// Clickable modifier here)


                }
            }
        }
    }
}





