package com.example.macc_project_app.data.reservation

import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    private val reservationRemoteDataSource: ReservationRemoteDataSource
) {
    private val TAG = ReservationRepository::class.java.simpleName

    // Mutex to make writes to cached values thread-safe.
    private val reservationsCacheMutex = Mutex()

    private var reservationsCache : List<Reservation> = emptyList()

    suspend fun getReservations(context: Context, refresh: Boolean): List<Reservation> {
        Log.d(TAG, "$refresh, $reservationsCache")
        if(refresh || reservationsCache.isEmpty()) {
            reservationsCacheMutex.withLock {
                reservationsCache = reservationRemoteDataSource.getReservations(
                    context
                ) ?: emptyList()
            }
        }

        return reservationsCache
    }

    suspend fun getReservation(restaurantId: Long): Reservation? {
        Log.d(TAG, "Find restaurant with id: $restaurantId")

        return reservationsCache.find { r -> r.restaurantId == restaurantId }
    }
}