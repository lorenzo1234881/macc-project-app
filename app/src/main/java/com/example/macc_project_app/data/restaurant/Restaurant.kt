package com.example.macc_project_app.data.restaurant

data class Restaurant(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val address: String,
    val distance: Double
)