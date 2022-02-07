package com.example.macc_project_app.domain

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.api.LogoutApi
import com.example.macc_project_app.data.reservation.ReservationRepository
import com.example.macc_project_app.data.restaurant.RestaurantRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    val logoutApi: LogoutApi,
    val restaurantRepository: RestaurantRepository,
    val reservationRepository: ReservationRepository
) {
    suspend fun logout(context: Context): Boolean? {
        val ret = logoutApi.logout(context)
        restaurantRepository.emptyCache()
        reservationRepository.emptyCache()

        return ret
    }

}