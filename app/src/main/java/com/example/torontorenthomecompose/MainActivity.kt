package com.example.torontorenthomecompose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.torontorenthome.util.HouseOperations
import com.example.torontorenthomecompose.ui.screen.AccountScreen
import com.example.torontorenthomecompose.ui.screen.FavoriteScreen
import com.example.torontorenthomecompose.ui.screen.ListScreen
import com.example.torontorenthomecompose.ui.screen.MapScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Add data to the db
        val houseOperations = HouseOperations(this)
        // houseOperations.generateRandomHousesAndUpload()
        setContent {
            MyApp()
        }
        if (!isLocationPermissionGranted()) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHostContainer(navController, Modifier.padding(innerPadding))
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = currentRoute(navController)

    // List of navigation items
    val navItems = listOf(
        BottomNavItem(Routes.MAP, Icons.Filled.LocationOn, "Map"),
        BottomNavItem(Routes.LIST, Icons.Filled.List, "List"),
        BottomNavItem(Routes.FAVORITES, Icons.Filled.Favorite, "Favorites"),
        BottomNavItem(Routes.ACCOUNT, Icons.Filled.AccountCircle, "Account")
    )

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigateSingleTopTo(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Routes.MAP, modifier = modifier) {
        composable(Routes.MAP) { MapScreen() }
        composable(Routes.LIST) { ListScreen() }
        composable(Routes.FAVORITES) { FavoriteScreen() }
        composable(Routes.ACCOUNT) { AccountScreen() } // Pass navController
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(this@navigateSingleTopTo.graph.startDestinationId) {
            saveState = true
        }
    }

@Composable
fun currentRoute(navController: NavHostController): String? {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    return currentBackStackEntry?.destination?.route
}


object Routes {
    const val MAP = "map"
    const val LIST = "list"
    const val FAVORITES = "favorites"
    const val ACCOUNT = "account"
}