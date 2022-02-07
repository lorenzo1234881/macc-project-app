package com.example.macc_project_app.domain

import android.content.Context
import com.example.macc_project_app.data.reservation.ReservationResult
import com.example.macc_project_app.data.reservation.ReservationRepository
import com.example.macc_project_app.data.restaurant.ReservedRestaurant
import javax.inject.Inject

class GetReservedRestaurantUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase
) {
    suspend operator fun invoke(context: Context, refresh: Boolean): List<ReservedRestaurant> {
        val reservationResults: List<ReservationResult> = reservationRepository.getReservations(context, refresh)
        val reservedRestaurants = ArrayList<ReservedRestaurant>()
        for (r in reservationResults) {
            reservedRestaurants.add(
                ReservedRestaurant(
                    restaurant=getNearbyRestaurantUseCase(r.reservation.restaurantId),
                    reservation=r.reservation)
            )
        }
        return reservedRestaurants
    }

    suspend operator fun invoke(restaurantId: Long): ReservationResult? {
        return reservationRepository.getReservation(restaurantId)
    }

    suspend fun existsRreservation(restaurantId: Long): Boolean {
        return reservationRepository.getReservation(restaurantId) != null
    }

}