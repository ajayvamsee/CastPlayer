package com.ajayvamsee.castplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import com.android.volley.Cache
import com.android.volley.Network
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.ImageLoader

private val TAG = CustomVolleyRequest.javaClass.name

/**
 * Created by Ajay Vamsee on 11/25/2022.
 * Time : 12:42 HRS
 */
class CustomVolleyRequest private constructor(context: Context) {

    private var requestQueue: RequestQueue?
    private var context: Context?
    val imageLoader: ImageLoader

    private fun getRequestQueue(): RequestQueue {
        Log.d(TAG, "getRequestQueue: ")
        if (requestQueue == null) {
            val cache: Cache = DiskBasedCache(context!!.cacheDir, 10 * 1024 * 1024)
            val network: Network = BasicNetwork(HurlStack())
            requestQueue = RequestQueue(cache, network)
            requestQueue!!.start()
        }
        return requestQueue!!
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var customVolleyRequest: CustomVolleyRequest? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): CustomVolleyRequest? {
            Log.d(TAG, "getInstance: ")
            if (customVolleyRequest == null) {
                customVolleyRequest = CustomVolleyRequest(context)
            }
            return customVolleyRequest
        }
    }

    init {
        Log.d(TAG, ": init block")
        this.context = context
        requestQueue = getRequestQueue()
        imageLoader = ImageLoader(requestQueue, object : ImageLoader.ImageCache {
            private val cache = LruCache<String, Bitmap>(20)
            override fun getBitmap(url: String?): Bitmap? {
                return cache[url]
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
                cache.put(url, bitmap)
            }

        })
    }
}