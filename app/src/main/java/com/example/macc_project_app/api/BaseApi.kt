package com.example.macc_project_app.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.macc_project_app.data.Restaurant
import org.json.JSONObject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseApi <T>{
    private val TAG = BaseApi::class.java.simpleName
    private val baseUrl = "https://ll328ii.pythonanywhere.com"

    protected inner class ResponseListener(private val cont : Continuation<T>): Response.Listener<JSONObject> {
        override fun onResponse(response: JSONObject?) {
            Log.d(TAG, "received $response")
            val parsedResponse = parseResponse(response)
            cont.resume(parsedResponse)
        }
    }

    protected inner class ErrorListener(private val cont : Continuation<T>): Response.ErrorListener {
        override fun onErrorResponse(error: VolleyError?) {
            cont.resumeWithException(Exception(error.toString()))
        }
    }

    suspend fun sendRequest(
        requestJsonObject: JSONObject,
        route:String,
        context: Context
    ) : T =
        /**
         * with 'suspendedCoroutine{}' we encapsulate the callback mechanism exposed by Volley
         * without the need to provide other callback functions
         * into coroutines, allowing the users of this class to use exclusively coroutines
         */
        suspendCoroutine { cont ->
            val volleySingleton = VolleySingleton.getInstance(context)

            Log.d(TAG, "sending $requestJsonObject")

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                baseUrl+route,
                requestJsonObject,
                ResponseListener(cont),
                ErrorListener(cont)
            )

            volleySingleton.addToRequestQueue(jsonObjectRequest)
        }

    protected abstract fun parseResponse(response: JSONObject?) : T
}