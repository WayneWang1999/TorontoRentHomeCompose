package com.example.torontorenthome.models


data class Buyer(
    val uid: String = "",
    val name:String,
    val email:String,
    val password:String,
    val favoriteHouseIds:List<String>,
)
