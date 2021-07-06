package com.example.imagegallery.data.datasource

import com.example.imagegallery.data.api.ApiService
import com.example.imagegallery.data.api.Method
import com.example.imagegallery.data.api.Request
import com.example.imagegallery.data.model.PhotoResponse
import com.example.imagegallery.data.model.Photos
import com.google.gson.Gson
import javax.inject.Inject

/**
 * Implementation of [PhotoDataSource], It fetches data from remote source
 * @param apiService Actual api service to get data from remote
 */
class RemotePhotoDataSource @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
) : PhotoDataSource {

    override fun getPhotos(queryString: String, page: Int): Photos {
        val queryParameters = mutableMapOf<String, String>()
        queryParameters[KEY_TEXT] = queryString
        queryParameters[KEY_PAGE] = page.toString()
        queryParameters[KEY_METHOD] = SEARCH_METHOD
        queryParameters[KEY_API_KEY] = API_KEY
        queryParameters[KEY_FORMAT] = FORMAT
        queryParameters[KEY_NO_JSON_CALLBACK] = NO_JSON_CALLBACK

        val request = Request(BASE_URL, Method.GET, queryParameters)

        val dataString = apiService.getData(request)
        val response = gson.fromJson(dataString, PhotoResponse::class.java)
        return response.photos
    }

    companion object {
        const val BASE_URL = "https://www.flickr.com/services/rest"
        const val API_KEY = "2932ade8b209152a7cbb49b631c4f9b6"
        const val SEARCH_METHOD = "flickr.photos.search"
        const val FORMAT = "json"
        const val NO_JSON_CALLBACK = "1"

        const val KEY_TEXT = "text"
        const val KEY_PAGE = "page"
        const val KEY_METHOD = "method"
        const val KEY_API_KEY = "api_key"
        const val KEY_FORMAT = "format"
        const val KEY_NO_JSON_CALLBACK = "nojsoncallback"
    }
}
