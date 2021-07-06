package com.example.imagegallery.data.api

import android.net.Uri
import java.net.URL

/**
 * The request is created to pass web service
 */
data class Request(
    val url: String,
    val method: Method,
    val queryParameters: Map<String, String>? = null,
    val connectTimeout: Int = 30000,
    val readTimeout: Int = 30000
) {
    fun getURL(): URL {
        val builder = Uri.parse(url).buildUpon()
        queryParameters?.forEach { (key, value) ->
            builder.appendQueryParameter(key, value)
        }
        return URL(builder.build().toString())
    }
}
