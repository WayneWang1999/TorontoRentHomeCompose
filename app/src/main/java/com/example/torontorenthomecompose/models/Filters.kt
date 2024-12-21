package com.example.torontorenthomecompose.models

data class Filters(
    val priceRange: IntRange,
    val bedrooms: Int,
    val bathrooms: Int,
    val propertyType: String
)