package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import org.json.JSONObject
import javax.inject.Inject

class MakeReservationApi  @Inject constructor(): BaseApi<Boolean?>() {

    private val TAG = MakeReservationApi::class.java.simpleName
    private val route = "/make-reservation"

    suspend fun sendReservation(restaurantId:Long, numberSeats: Int, context: Context): Boolean? {
        val reservation = JSONObject()

        reservation.put("restaurantid", restaurantId)
        reservation.put("number_seats", numberSeats)

        return super.sendRequest(reservation, route, Request.Method.POST, context)
    }

    override fun parseResponse(response: JSONObject?): Boolean? {
        if (response != null || response === {}) {

            return response.getBoolean("reserved")
        }

        return null
    }

}