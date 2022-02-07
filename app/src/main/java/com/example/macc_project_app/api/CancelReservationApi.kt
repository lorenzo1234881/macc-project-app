package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import com.example.macc_project_app.data.reservation.ReservationResult
import org.json.JSONObject
import javax.inject.Inject

class CancelReservationApi @Inject constructor(): BaseApi<Boolean?>(){

    private val route = "/cancel-reservation"

    suspend fun sendCancellation(reservationResult: ReservationResult, context: Context): Boolean? {
        val reservationJsonObj = JSONObject()
        reservationJsonObj.put("restaurantid", reservationResult.reservation.restaurantId)

        return super.sendRequest(reservationJsonObj, route, Request.Method.POST, context)
    }

    override fun parseResponse(response: JSONObject?): Boolean? {
        if (response != null || response === {}) {

            return response.getBoolean("reserved")
        }

        return null
    }
}