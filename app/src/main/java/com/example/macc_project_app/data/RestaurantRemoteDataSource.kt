package com.example.macc_project_app.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.VolleyError
import com.example.macc_project_app.api.NearbyRestaurantApi
import org.json.JSONObject
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