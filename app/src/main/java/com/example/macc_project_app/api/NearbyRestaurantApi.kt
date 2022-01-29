package com.example.macc_project_app.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.macc_project_app.data.Restaurant
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NearbyRestaurantApi : BaseApi<ArrayList<Restaurant>?> {
    @Inject constructor()

    private val route = "/nearby-restaurants"

    suspend fun getNearbyRestaurants(
        latitude : String,
        longitude : String,
        context: Context
    ) : ArrayList<Restaurant>? {
        val locationUser = JSONObject()

        locationUser.put("latitude", latitude)
        locationUser.put("longitude", longitude)

        return super.sendRequest(locationUser, route, context)
    }

    override fun parseResponse(response: JSONObject?) : ArrayList<Restaurant>?
    {
        if(response != null || response === {}) {

            val jsonArray = response.getJSONArray("restaurants")
            val jsonArrayLen = jsonArray.length()
            val restaurants = ArrayList<Restaurant>(jsonArrayLen)

            for (i in 0 until jsonArrayLen) {
                val restaurantJsonObject = jsonArray.getJSONObject(i)
                restaurants.add (Restaurant(
                    restaurantJsonObject.getLong("id"),
                    restaurantJsonObject.getString("name"),
                    restaurantJsonObject.getString("description"),
                    restaurantJsonObject.getString("path_image"),
                    restaurantJsonObject.getString("address"),
                    restaurantJsonObject.getDouble("distance")
                    ))
            }

            return restaurants
        }

        return null
    }

}