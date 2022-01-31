package com.example.macc_project_app.data.restaurant

import android.content.Context
import com.example.macc_project_app.api.NearbyRestaurantApi
import javax.inject.Inject

class RestaurantRemoteDataSource @Inject constructor(
    val nearbyRestaurantApi: NearbyRestaurantApi
) {

    suspend fun getNearbyRestaurants (
        latitude : String,
        longitude : String,
        context: Context
    ) : List<Restaurant>? {
        return nearbyRestaurantApi.getNearbyRestaurants(
            latitude, longitude, context
        )
    }

}