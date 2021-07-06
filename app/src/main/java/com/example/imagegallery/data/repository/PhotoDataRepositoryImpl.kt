package com.example.imagegallery.data.repository

import com.example.imagegallery.data.datasource.PhotoDataSource
import com.example.imagegallery.data.model.Photos
import com.example.imagegallery.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of [PhotoDataRepository]
 */
class PhotoDataRepositoryImpl @Inject constructor(
    private val dataSource: PhotoDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PhotoDataRepository {

    override suspend fun getPhotos(queryString: String, page: Int): Photos {
        return withContext(ioDispatcher) { dataSource.getPhotos(queryString, page) }
    }
}
