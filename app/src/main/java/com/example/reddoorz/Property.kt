package com.example.reddoorz

/**
 * Data class representing a Hotel Property fetched from Firestore.
 */
data class Property(
    val id: String = "",
    val prop_name: String = "",
    val prop_city: String = "",
    val prop_street: String = "",
    val base_price: Double = 0.0,
    val rating: Double = 0.0,
    val part_id: String = "",
    val image_urls: List<String> = listOf()
)
