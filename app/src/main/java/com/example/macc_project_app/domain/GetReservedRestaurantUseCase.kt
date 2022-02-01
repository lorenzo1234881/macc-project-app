package com.example.macc_project_app.domain

import android.content.Context
import com.example.macc_project_app.data.reservation.Reservation
import com.example.macc_project_app.data.reservation.ReservationRepository
import com.example.macc_project_app.data.restaurant.ReservedRestaurant
import com.example.macc_project_app.data.restaurant.Restaurant
import javax.inject.Inject

class GetReservedRestaurantUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase
) {
    suspend operator fun invoke(context: Context, refresh: Boolean): List<ReservedRestaurant> {
        val reservations: List<Reservation> = reservationRepository.getReservations(context, refresh)
        val reservedRestaurants = ArrayList<ReservedRestaurant>()
        for (r in reservations) {
            reservedRestaurants.add(
                ReservedRestaurant(
                    restaurant=getNearbyRestaurantUseCase(r.restaurantId),
                    numberSeats=r.numberSeats))
        }
        return reservedRestaurants
    }

    suspend operator fun invoke(restaurantId: Long): Boolean {
        return reservationRepository.getReservation(restaurantId) != null
    }
}