package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import com.example.macc_project_app.data.restaurant.Restaurant
import org.json.JSONObject
import javax.inject.Inject

class NearbyRestaurantApi @Inject constructor() : BaseApi<ArrayList<Restaurant>?>() {

    private val route = "/nearby-restaurants"

    suspend fun getNearbyRestaurants(
        latitude : String,
        longitude : String,
        context: Context
    ) : ArrayList<Restaurant>? {
        val locationUser = JSONObject()

        locationUser.put("latitude", latitude)
        locationUser.put("longitude", longitude)

        return super.sendRequest(locationUser, route, Request.Method.POST, context)
    }

    override fun parseResponse(response: JSONObject?) : ArrayList<Restaurant>?
    {
        if(response != null || response === {}) {

            val jsonArray = response.getJSONArray("restaurants")
            val jsonArrayLen = jsonArray.length()
            val restaurants = ArrayList<Restaurant>(jsonArrayLen)

            for (i in 0 until jsonArrayLen) {
                val restaurantJsonObject = jsonArray.getJSONObject(i)
                restaurants.add (
                    Restaurant(
                    restaurantJsonObject.getLong("id"),
                    restaurantJsonObject.getString("name"),
                    restaurantJsonObject.getString("description"),
                    restaurantJsonObject.getString("path_image"),
                    restaurantJsonObject.getString("address"),
                    restaurantJsonObject.getDouble("distance")
                    )
                )
            }

            return restaurants
        }

        return null
    }

}