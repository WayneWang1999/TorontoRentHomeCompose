package com.example.torontorenthomecompose.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.ListScreenViewModel


@Composable
fun ListScreen() {
    val viewModel: ListScreenViewModel = viewModel()
    val houseList = viewModel.houseList.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading...", fontSize = 20.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(houseList.value.size) { index ->
                val house = houseList.value[index]
                HouseItem(
                    imageUrl = house.imageUrl,
                    price = house.price,
                    bedrooms = house.bedrooms,
                    address = house.address,
                    bathrooms = house.bathrooms,
                    area = house.area,
                    createTime = house.createTime,
                    onFavoriteClick = { /* Add your logic */ }
                )
            }
        }
    }
}




