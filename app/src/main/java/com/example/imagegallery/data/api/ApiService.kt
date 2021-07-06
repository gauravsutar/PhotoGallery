package com.example.imagegallery.data.api

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HttpsURLConnection

/**
 * Class is responsible for calling web service and get data from server.
 */
@Singleton
class ApiService @Inject constructor() {

    fun getData(request: Request): String? {
        val url = request.getURL()
        var responseString: String? = null

        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = request.connectTimeout
        urlConnection.readTimeout = request.readTimeout
        urlConnection.requestMethod = request.method.toString()
        val responseCode: Int = urlConnection.responseCode

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            responseString = readStream(urlConnection.inputStream)
            Log.v("Response -> ", responseString)
        }

        return responseString
    }

    private fun readStream(inputStream: InputStream): String {
        var reader: BufferedReader? = null
        val response = StringBuffer()
        try {
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = ""
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            reader?.close()
        }
        return response.toString()
    }
}
