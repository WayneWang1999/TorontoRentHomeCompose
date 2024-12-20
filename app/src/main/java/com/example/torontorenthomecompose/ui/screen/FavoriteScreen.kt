package com.example.torontorenthomecompose.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.FavoriteScreenViewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.ListScreenViewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel


@Composable
fun FavoriteScreen(
    userStateViewModel: UserStateViewModel
) {
    val favoriteScreenViewModel: FavoriteScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoriteScreenViewModel(userStateViewModel) as T
            }
        }
    )
   // val favoriteScreenViewModel: FavoriteScreenViewModel = viewModel()
    val houseList = favoriteScreenViewModel.favoriteHouses.collectAsState()
    val isLoading = favoriteScreenViewModel.isLoading.collectAsState()
    val isLoggedIn by userStateViewModel.isLoggedIn.collectAsState()
    if(isLoggedIn){
        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Loading...", fontSize = 20.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(houseList.value.size) { index ->
                    val house = houseList.value[index]
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
                        isFavorite = true
                    )
                }
            }
        }

    }else{
        Log.d("UserLogin","User logout")

    }

}
