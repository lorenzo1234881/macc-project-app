package com.example.macc_project_app.domain

import android.content.Context
import com.example.macc_project_app.data.Restaurant
import com.example.macc_project_app.data.RestaurantRepository
import javax.inject.Inject

class GetNearbyRestaurantUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
){
    suspend operator fun invoke(latitude: String,
                                longitude: String,
                                context: Context,
                                refresh:Boolean): List<Restaurant> {
        return restaurantRepository.getNearbyRestaurant(latitude, longitude, context, refresh)
    }

    suspend fun invoke(restaurantId: Long): Restaurant {
        return restaurantRepository.getRestaurant(restaurantId)
    }
}