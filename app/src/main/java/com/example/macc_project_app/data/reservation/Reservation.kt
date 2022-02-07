package com.example.macc_project_app.data.reservation

class Reservation(
    val restaurantId: Long,
    var numberSeats: Int,
    var year: Int,
    var month: Int,
    var dayOfMonth: Int,
    var hour: Int,
    var minute: Int
    ) {
    constructor(restaurantId: Long) : this(
        restaurantId,0,0,0,0,0,0
    )
}

data class ReservationResult(
    val id: Long,
    val reservation: Reservation
)