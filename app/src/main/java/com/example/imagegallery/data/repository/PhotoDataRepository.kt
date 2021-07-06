package com.example.imagegallery.data.repository

import com.example.imagegallery.data.model.Photos

/**
 * Repository to handle photo data
 */
interface PhotoDataRepository {

    suspend fun getPhotos(queryString: String, page: Int): Photos
}
