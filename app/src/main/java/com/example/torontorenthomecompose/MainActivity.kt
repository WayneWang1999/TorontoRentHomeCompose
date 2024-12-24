package com.example.torontorenthomecompose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.torontorenthome.util.HouseOperations
import com.example.torontorenthomecompose.ui.screen.AccountScreen
import com.example.torontorenthomecompose.ui.screen.DetailScreen
import com.example.torontorenthomecompose.ui.screen.FavoriteScreen
import com.example.torontorenthomecompose.ui.screen.FilterScreen
import com.example.torontorenthomecompose.ui.screen.ListScreen
import com.example.torontorenthomecompose.ui.screen.MapScreen
import com.example.torontorenthomecompose.ui.screen.SignUpScreen
import com.example.torontorenthomecompose.ui.screen.models.BottomNavItem
import com.example.torontorenthomecompose.ui.screen.models.Routes
import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // private val userStateViewModel: UserStateViewModel by viewModels()  // Initialize the UserStateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp() //
//            // Create an instance of HouseOperations
//            val houseOperations = HouseOperations(this)
//            houseOperations.generateRandomHousesAndUpload()
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
    val currentRoute = currentRoute(navController)

    Scaffold(
        //Note:The BottomNavigationBar adjusts its visibility based on the current route
        // (FILTER and DETAIL screens do not show it). This provides a better user experience.
        bottomBar = {
            if (currentRoute !in listOf(Routes.Filter.route) && !isDetailRoute(currentRoute)) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHostContainer(
            navController,
            Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = currentRoute(navController)
    // List of navigation items
    val navItems = listOf(
        BottomNavItem(Routes.Map.route, Icons.Filled.LocationOn, "Map"),
        BottomNavItem(Routes.List.route, Icons.AutoMirrored.Filled.List, "List"),
        BottomNavItem(Routes.Favorites.route, Icons.Filled.Favorite, "Favorites"),
        BottomNavItem(Routes.Account.route, Icons.Filled.AccountCircle, "Account")
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
fun NavHostContainer(
    navController: NavHostController,
    modifier: Modifier,
) {
   // val userStateViewModel: UserStateViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Routes.Map.route,
        modifier = modifier
    ) {
        composable(Routes.Map.route) {
            MapScreen(
                onFilterClick = { navController.navigate("filter") },
                navController
            )
        }
        composable(Routes.List.route) {
            ListScreen(
                onFilterClick = { navController.navigate("filter") },
                navController,
            )
        }

        composable(Routes.Favorites.route) {
            FavoriteScreen(
                navController
            )
        }
        composable(Routes.Account.route) {
            AccountScreen(
                navController
            )
        }

//Note:   implemented a dynamic route for the DetailScreen by using detail/{houseId}
//        and extracting houseId from backStackEntry.arguments.
        composable(Routes.Detail.ROUTE) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId")
            DetailScreen(
                onBackClick = { navController.popBackStack() },
                houseId = houseId
            )
        }

        composable(Routes.Filter.route) {
            FilterScreen(
                onBackClick = { navController.popBackStack() },
                navController,
            )
        }
        composable(Routes.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                navController
            )
        }

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

fun isDetailRoute(route: String?): Boolean {
    return route?.startsWith("detail/") == true
}

