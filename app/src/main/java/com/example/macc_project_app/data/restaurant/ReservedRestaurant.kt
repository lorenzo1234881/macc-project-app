package com.example.macc_project_app.data.restaurant

import com.example.macc_project_app.data.reservation.Reservation

data class ReservedRestaurant(
    val restaurant: Restaurant,
    val reservation: Reservation
)

