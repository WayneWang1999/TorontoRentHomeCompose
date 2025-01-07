package com.example.torontorenthomecompose.ui.screen

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val isLoggedIn by userStateViewModel.isLoggedIn.collectAsState()
    val favoriteHouseIds by userStateViewModel.favoriteHouseIds.collectAsState()
    val filters by userStateViewModel.filters.collectAsState()
    val houseList by listScreenViewModel.houseList.collectAsState()
    val isLoading by listScreenViewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredHouses = remember(searchQuery, filters, houseList) {
        houseList.filter { house ->
            val matchesSearchQuery =
                searchQuery.isBlank() || house.address.contains(searchQuery, ignoreCase = true)
            val matchesFilters = filters?.let {
                house.price in it.priceRange &&
                        house.bedrooms >= it.bedrooms &&
                        house.bathrooms >= it.bathrooms
            } ?: true
            matchesSearchQuery && matchesFilters
        }
    }

    val listState = rememberLazyListState()
    val showButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.onSecondary),
                //   .background(Color(0xFF1E88E5)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TORH.ca",
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(0.25f),
                    //    color = MaterialTheme.colorScheme.onPrimary,
                    style=MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search...") },
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .weight(0.6f),
                )

                val badgeCount = if (filters != null) 1 else 0
                FilterIconWithBadge(onFilterClick = { onFilterClick() }, badgeCount = badgeCount)
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
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

                        Box(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Routes.Detail(house.houseId).route) {
                                        launchSingleTop = true
                                    }
                                }
                                .padding(8.dp)
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
                                    userStateViewModel.toggleFavorite(
                                        houseId
                                    )
                                },
                                isFavorite = isFavorite,
                                modifier = Modifier
                                    .animateContentSize()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button inside the same Box
        if (showButton) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
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




