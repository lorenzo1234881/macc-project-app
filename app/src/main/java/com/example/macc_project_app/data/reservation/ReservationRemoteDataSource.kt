package com.example.macc_project_app.data.reservation

import android.content.Context
import com.example.macc_project_app.api.GetReservationsApi
import javax.inject.Inject

class ReservationRemoteDataSource @Inject constructor(
    private val getReservationsApi: GetReservationsApi
) {
    suspend fun getReservations(context: Context): List<Reservation>? {
        return getReservationsApi.getReservations(context)
    }
}