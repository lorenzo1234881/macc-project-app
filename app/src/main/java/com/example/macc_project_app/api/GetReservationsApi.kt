package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import com.example.macc_project_app.data.reservation.Reservation
import com.example.macc_project_app.data.restaurant.Restaurant
import org.json.JSONObject
import javax.inject.Inject

class GetReservationsApi @Inject constructor() : BaseApi<ArrayList<Reservation>?>() {

    private val route = "/get-reservations"

    suspend fun getReservations(context: Context): ArrayList<Reservation>? {
        return super.sendRequest(null, route, Request.Method.GET, context)
    }

    override fun parseResponse(response: JSONObject?): ArrayList<Reservation>? {
        if(response != null || response === {}) {

            val jsonArray = response.getJSONArray("reservations")
            val jsonArrayLen = jsonArray.length()
            val reservations = ArrayList<Reservation>(jsonArrayLen)

            for (i in 0 until jsonArrayLen) {
                val reservationJsonObject = jsonArray.getJSONObject(i)
                reservations.add (
                    Reservation(
                        reservationJsonObject.getLong("id"),
                        reservationJsonObject.getLong("restaurantid"),
                        reservationJsonObject.getInt("number_seats")
                    )
                )
            }

            return reservations
        }

        return null
    }
}