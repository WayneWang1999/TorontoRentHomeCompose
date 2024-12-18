package com.example.torontorenthome.models

data class Seller(
    val name:String,
    val email:String,
    val password:String,
    val ownerHouseIds:List<String>,
)
