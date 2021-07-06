package com.example.imagegallery.data.datasource

import com.example.imagegallery.data.model.Photos

/**
 * Abstract data source for photo data
 */
interface PhotoDataSource {

    fun getPhotos(queryString: String, page: Int): Photos
}
