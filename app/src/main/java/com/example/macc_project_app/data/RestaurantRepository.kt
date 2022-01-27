package com.example.macc_project_app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RestaurantRepository @Inject constructor(
    val restaurantRemoteDataSource: RestaurantRemoteDataSource,
) {
    private val TAG = RestaurantRepository::class.java.simpleName

    // Mutex to make writes to cached values thread-safe.
    private val restaurantsCacheMutex = Mutex()

    private var restaurantsCache : List<Restaurant> = emptyList()

    suspend fun getNearbyRestaurant(
        latitude: String,
        longitude: String,
        context: Context
    ): List<Restaurant> {
        restaurantsCacheMutex.withLock {
            restaurantsCache = restaurantRemoteDataSource.getNearbyRestaurants(
                latitude,
                longitude,
                context
            ) ?: emptyList()
        }

        return restaurantsCache
    }

    suspend fun getRestaurant(restaurantId: Long): Restaurant {
        Log.d(TAG, "Find restaurant with id: $restaurantId")

        val restaurant = restaurantsCache.find { r -> r.id == restaurantId }
        return restaurant!!
    }
}