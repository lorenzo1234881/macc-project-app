package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import org.json.JSONObject
import javax.inject.Inject

class LogoutApi @Inject constructor(): BaseApi<Boolean?>() {

    private val route = "/logout"

    suspend fun logout(context: Context): Boolean? {
        return super.sendRequest(null, route, Request.Method.GET, context)
    }

    override fun parseResponse(response: JSONObject?): Boolean? {
        if (response != null || response === {}) {

            return response.getBoolean("auth")
        }

        return null
    }
}