package com.example.macc_project_app.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.macc_project_app.data.Restaurant
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NearbyRestaurantApi @Inject constructor(){
    private val url = "https://ll328ii.pythonanywhere.com/nearby-restaurants"

    inner class ResponseListener(private val cont : Continuation<ArrayList<Restaurant>?>): Response.Listener<JSONObject> {
        override fun onResponse(response: JSONObject?) {
            Log.d("VOLLEY response", response.toString())
            val restaurantsList = parseResponse(response)
            cont.resume(restaurantsList)
        }
    }

    inner class ErrorListener(private val cont : Continuation<ArrayList<Restaurant>?>): Response.ErrorListener {
        override fun onErrorResponse(error: VolleyError?) {
            cont.resumeWithException(Exception(error.toString()))
        }
    }

    suspend fun getNearbyRestaurants(
        latitude : String,
        longitude : String,
        context: Context
    ) : ArrayList<Restaurant>? =
        /**
         * with 'suspendedCoroutine{}' we encapsulate the callback mechanism exposed by Volley
         * without the need to provide other callback functions
         * into coroutines, allowing the users of this class to use exclusively coroutines
         */
        suspendCoroutine { cont ->
        val locationUser = JSONObject()
        val volleySingleton = VolleySingleton.getInstance(context)

        locationUser.put("latitude", latitude)
        locationUser.put("longitude", longitude)

         Log.i("VOLLEY", "sending " + locationUser.toString() )

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            locationUser,
            ResponseListener(cont),
            ErrorListener(cont)
        )

        volleySingleton.addToRequestQueue(jsonObjectRequest)
    }

    private fun parseResponse(response: JSONObject?) : ArrayList<Restaurant>?
    {
        if(response != null || response === {}) {

            val jsonArray = response.getJSONArray("restaurants")
            val jsonArrayLen = jsonArray.length()
            val restaurants = ArrayList<Restaurant>(jsonArrayLen)

            for (i in 0 until jsonArrayLen) {
                val restaurantJsonObject = jsonArray.getJSONObject(i)
                restaurants.add (Restaurant(
                    restaurantJsonObject.getString("name"),
                    restaurantJsonObject.getString("description"),
                    restaurantJsonObject.getString("path_image"))
                )
            }

            return restaurants
        }

        return null
    }


}