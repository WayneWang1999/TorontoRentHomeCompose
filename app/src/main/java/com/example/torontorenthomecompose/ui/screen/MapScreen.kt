package com.example.torontorenthomecompose.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torontorenthomecompose.R
import com.example.torontorenthomecompose.ui.screen.viewmodels.MapScreenViewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState



@Composable
fun MapScreen(
    userStateViewModel: UserStateViewModel,
) {
    val mapScreenViewModel: MapScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapScreenViewModel(userStateViewModel) as T
            }
        }
    )
    // *********Use StateFlow to define state variables
    val houses by mapScreenViewModel.houseLocations.collectAsState()
    val selectedHouse by mapScreenViewModel.selectedHouse.collectAsState()
    val isLoggedIn by userStateViewModel.isLoggedIn.collectAsState()
    val favoriteHouseIds = userStateViewModel.favoriteHouseIds.collectAsState()
    // Search query state
    var searchQuery by remember { mutableStateOf("") }
//    val filteredHouses = if (searchQuery.isBlank()) {
//       // houseList
//    } else {
////       // houseList.filter { house ->
////            house.address.contains(searchQuery, ignoreCase = true) || house.price.toString().contains(searchQuery)
//        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Light Gray Background
    ) {
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


            // Filter Icon
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .weight(0.15f)
                    .clickable { /* Handle filter click */ }
                    .background(Color(0xFF1565C0), shape = androidx.compose.foundation.shape.CircleShape), // Circular button with blue background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_filter),
                    contentDescription = "Filter",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }



        // MapView
        Box(modifier = Modifier.fillMaxSize()) {
            // GoogleMap fills the background
            val toronto = LatLng(43.6532, -79.3832)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(toronto, 10f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    mapScreenViewModel.setSelectedHouse(null) // Clear the selected house
                }
            ) {
                // Add markers for houses locations
                houses.forEach { house ->
                    Marker(
                        state = MarkerState(LatLng(house.latitude, house.longitude)),
                        title = house.address,
                        snippet = house.address,
                        onClick = {
                            mapScreenViewModel.setSelectedHouse(house)
                            true // Return true to indicate that the click was handled
                        }
                    )
                }
            }
            selectedHouse?.let { house ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    val isFavorite = if (isLoggedIn) {
                        favoriteHouseIds.value.contains(house.houseId)
                    } else {
                        false
                    }
                    HouseItem(
                        houseId=house.houseId,
                        imageUrl = house.imageUrl,
                        price = house.price,
                        bedrooms = house.bedrooms,
                        address = house.address,
                        bathrooms = house.bathrooms,
                        area = house.area,
                        createTime = house.createTime,
                        onFavoriteClick = { houseId ->
                            userStateViewModel.toggleFavorite(houseId) },
                        isFavorite = isFavorite
                    )
                }
            }
        }

    }

}