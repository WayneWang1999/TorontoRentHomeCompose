package com.example.torontorenthomecompose.ui.screen.models

data class Filters(
    val priceRange: IntRange,
    val bedrooms: Int,
    val bathrooms: Int,
    val propertyType: String
)