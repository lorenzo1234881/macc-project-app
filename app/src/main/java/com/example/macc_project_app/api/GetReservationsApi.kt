package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import com.example.macc_project_app.data.reservation.Reservation
import com.example.macc_project_app.data.reservation.ReservationResult
import org.json.JSONObject
import javax.inject.Inject

class GetReservationsApi @Inject constructor() : BaseApi<ArrayList<ReservationResult>?>() {

    private val route = "/get-reservations"

    suspend fun getReservations(context: Context): ArrayList<ReservationResult>? {
        return super.sendRequest(null, route, Request.Method.GET, context)
    }

    override fun parseResponse(response: JSONObject?): ArrayList<ReservationResult>? {
        if(response != null || response === {}) {

            val jsonArray = response.getJSONArray("reservations")
            val jsonArrayLen = jsonArray.length()
            val reservations = ArrayList<ReservationResult>(jsonArrayLen)

            for (i in 0 until jsonArrayLen) {
                val reservationJsonObject = jsonArray.getJSONObject(i)
                reservations.add (
                    ReservationResult(
                        reservationJsonObject.getLong("id"),
                        Reservation(
                            reservationJsonObject.getLong("restaurantid"),
                            reservationJsonObject.getInt("number_seats"),
                            reservationJsonObject.getInt("year"),
                            reservationJsonObject.getInt("month"),
                            reservationJsonObject.getInt("day"),
                            reservationJsonObject.getInt("hour"),
                            reservationJsonObject.getInt("minute"),
                        )
                    )
                )
            }

            return reservations
        }

        return null
    }
}