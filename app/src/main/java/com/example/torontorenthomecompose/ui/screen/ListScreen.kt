package com.example.torontorenthomecompose.ui.screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.torontorenthomecompose.ui.screen.models.Routes
import com.example.torontorenthomecompose.ui.screen.viewmodels.ListScreenViewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    userStateViewModel: UserStateViewModel,
    onFilterClick: () -> Unit,
    navController: NavHostController,
    listScreenViewModel: ListScreenViewModel = hiltViewModel(),

) {

    // variables from the userStateViewModel
    val isLoggedIn by userStateViewModel.isLoggedIn.collectAsState()
    val favoriteHouseIds by userStateViewModel.favoriteHouseIds.collectAsState()
    val filters by userStateViewModel.filters.collectAsState()// Filters from the UserStateViewModel

    // variables from the listScreenViewModel
    val houseList by listScreenViewModel.houseList.collectAsState()
    val isLoading by listScreenViewModel.isLoading.collectAsState()

    // Local variables
    var searchQuery by remember { mutableStateOf("") }//Search query state


// Apply both search query and filters
    val filteredHouses = remember(searchQuery, filters, houseList) {
        houseList.filter { house ->
            val matchesSearchQuery =
                searchQuery.isBlank() || house.address.contains(searchQuery, ignoreCase = true)
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

    Box(modifier = Modifier.fillMaxSize()) {
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
                CircularProgressIndicator()
            }
        } else {
            /*To calculate whether the user has passed the first item, use LazyColumn's LazyListState and check
            if listState.firstVisibleItemIndex > 0.This solution is not as efficient as it could be,
            because the composable function reading showButton recomposes as often as firstVisibleItemIndex changes -
            which happens frequently when scrolling. Instead,you want the function to recompose only when the condition
            changes between true and false.There's an API that allows you to do this: the derivedStateOf API.listState
            is an observable Compose State. Your calculation, showButton, also needs to be a Compose State since you want
             the UI to recompose when its value changes, and show or hide the button.Use derivedStateOf when you want a
             Compose State that's derived from another State. The derivedStateOf calculation block is executed every time
             the internal state changes, but the composable function only recomposes when the result of the calculation
             is different from the last one. This minimizes the amount of times functions reading showButton recompose.
           Using the derivedStateOf API in this case is a better and more efficient alternative.
           You'll also wrap the call with the remember API, so the calculated value survives recomposition.
            */
            val listState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(filteredHouses.size) { index ->
                    val house = filteredHouses[index]
                    val isFavorite = if (isLoggedIn) {
                        favoriteHouseIds.contains(house.houseId)
                    } else {
                        false
                    }

                    // Animating the scale of the item
                    val scale = remember { androidx.compose.animation.core.Animatable(0.8f) }
                    LaunchedEffect(Unit) {
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .scale(scale.value) // Apply the scaling animation
                            .clickable {
                                navController.navigate(Routes.Detail(house.houseId).route) {
                                    launchSingleTop = true
                                }
                            }
                            .padding(8.dp) // Add some padding for better spacing
                    ) {
                        HouseItem(
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
                            isFavorite = isFavorite,
                            modifier = Modifier
                                .animateContentSize() // Smooth transition for content changes
                                .padding(8.dp) // Padding around each item
                        )
                    }
                }
            }
            // Show the button if the first visible item is past
            // the first item. We use a remembered derived state to
            // minimize unnecessary compositions
            val showButton by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex > 0
                }
            }
            if (showButton) {
                val coroutineScope = rememberCoroutineScope()
                FloatingActionButton(
                    //    backgroundColor = MaterialTheme.colors.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.Red,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(bottom = 8.dp),
                    onClick = {
                        coroutineScope.launch {
                            listState.scrollToItem(0)
                        }
                    }
                ) {
                    Text("Up!")
                }
            }

        }


    }

}





