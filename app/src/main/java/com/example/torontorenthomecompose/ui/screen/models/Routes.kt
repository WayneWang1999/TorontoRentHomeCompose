package com.example.torontorenthomecompose.ui.screen.models

sealed class Routes(val route: String) {
    object Map : Routes("map")
    object ListHouse : Routes("list")
    object Favorites : Routes("favorites")
    object Account : Routes("account")
    object SignUp : Routes("signup")
    object Filter : Routes("filter") // No parameters, so keep it as an object
    data class Detail(val houseId: String) : Routes("detail/$houseId")
    {
        companion object {
            const val route = "detail/{houseId}"
        }
    }
}