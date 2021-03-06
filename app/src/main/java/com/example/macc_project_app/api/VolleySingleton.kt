package com.example.macc_project_app.api

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy

class VolleySingleton constructor(context: Context){
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context) =
            // Only one thread at the time can call getInstance() if INSTANCE is null
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also {
                    it.mCookieManager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
                    CookieHandler.setDefault(it.mCookieManager)
                    INSTANCE = it
                }
            }

    }

    private lateinit var mCookieManager: CookieManager

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap?) {
                    cache.put(url, bitmap)
                }
            })
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

}