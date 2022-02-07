package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import com.example.macc_project_app.data.reservation.Reservation
import org.json.JSONObject
import javax.inject.Inject

class MakeReservationApi  @Inject constructor(): BaseApi<Boolean?>() {

    private val TAG = MakeReservationApi::class.java.simpleName
    private val route = "/make-reservation"

    suspend fun sendReservation(reservation: Reservation, context: Context): Boolean? {
        val reservationJsonObj = JSONObject()

        reservationJsonObj.put("restaurantid", reservation.restaurantId)
        reservationJsonObj.put("number_seats", reservation.numberSeats)
        reservationJsonObj.put("year", reservation.year)
        reservationJsonObj.put("month", reservation.month)
        reservationJsonObj.put("dayOfMonth", reservation.dayOfMonth)
        reservationJsonObj.put("hour", reservation.hour)
        reservationJsonObj.put("minute", reservation.minute)

        return super.sendRequest(reservationJsonObj, route, Request.Method.POST, context)
    }

    override fun parseResponse(response: JSONObject?): Boolean? {
        if (response != null || response === {}) {

            return response.getBoolean("reserved")
        }

        return null
    }

}