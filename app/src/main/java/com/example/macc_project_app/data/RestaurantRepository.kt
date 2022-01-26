package com.example.macc_project_app.data

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    val restaurantRemoteDataSource: RestaurantRemoteDataSource,
) {
    // Mutex to make writes to cached values thread-safe.
    private val restaurantsCacheMutex = Mutex()

    private var restaurantsCache : List<Restaurant> = emptyList()

    suspend fun getNearbyRestaurant(
        latitude: String,
        longitude: String,
        context: Context,
        refresh: Boolean = false
    ): List<Restaurant>? {
        if(refresh || restaurantsCache.isEmpty()) {
            val networkResult =  restaurantRemoteDataSource.getNearbyRestaurants(
                latitude,
                longitude,
                context
            )
            restaurantsCacheMutex.withLock {
                if(networkResult != null) {
                    restaurantsCache = networkResult
                }
                else {
                    restaurantsCache = emptyList()
                }
            }
        }

        return restaurantsCache
    }
}