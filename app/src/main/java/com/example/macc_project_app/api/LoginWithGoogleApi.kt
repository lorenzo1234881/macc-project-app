package com.example.macc_project_app.api

import android.content.Context
import com.android.volley.Request
import org.json.JSONObject
import javax.inject.Inject


class LoginWithGoogleApi @Inject constructor(): BaseApi<Boolean?>() {

    private val TAG = LoginWithGoogleApi::class.java.simpleName
    private val route = "/token-signin"

    suspend fun sendToken(idToken: String, context: Context) : Boolean? {
        val token = JSONObject()

        token.put("id_token", idToken)

        return super.sendRequest(token, route, Request.Method.POST, context)
    }

    override fun parseResponse(response: JSONObject?): Boolean? {
        if (response != null || response === {}) {

            return response.getBoolean("auth")
        }

        return null
    }
}