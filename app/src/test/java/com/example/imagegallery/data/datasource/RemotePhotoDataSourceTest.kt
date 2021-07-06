package com.example.imagegallery.data.datasource

import com.example.imagegallery.FileReader
import com.example.imagegallery.data.api.ApiService
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RemotePhotoDataSourceTest {

    @Mock
    lateinit var apiService: ApiService


    lateinit var gson: Gson

    lateinit var subject: RemotePhotoDataSource

    @Before
    fun setup() {
        gson = Gson()
        subject = RemotePhotoDataSource(apiService, gson)
    }

    @Test
    fun testGetPhotosSuccessfulResponse() {
        `when`(apiService.getData(any())).thenReturn(
            FileReader.readStringFromFile(
                path = "json/success.json"
            )
        )
        val photos = subject.getPhotos(QUERY, PAGE)
        assertNotNull(photos)
        assertEquals(1, photos.page)
        assertNotNull(photos.photo)
        assertTrue(photos.photo.isNotEmpty())
    }

    @Test
    fun testGetPhotosSuccessfulResponseWithEmptyList() {
        `when`(apiService.getData(any())).thenReturn(
            FileReader.readStringFromFile(
                path = "json/success_empty_list.json"
            )
        )
        val photos = subject.getPhotos(QUERY, PAGE)
        assertNotNull(photos)
        assertEquals(1, photos.page)
        assertNotNull(photos.photo)
        assertTrue(photos.photo.isEmpty())
    }

    @Test(expected = Exception::class)
    fun testGetPhotosSuccessfulResponseWithMalformedJson() {
        `when`(apiService.getData(any())).thenReturn("{{}")
        subject.getPhotos(QUERY, PAGE)
    }

    @Test(expected = Exception::class)
    fun testGetPhotosSuccessfulNullResponse() {
        `when`(apiService.getData(any())).thenReturn(null)
        subject.getPhotos(QUERY, PAGE)
    }

    @Test(expected = Exception::class)
    fun testGetPhotosThrowException() {
        `when`(apiService.getData(any())).thenThrow(Exception::class.java)
        subject.getPhotos(QUERY, PAGE)
    }

    companion object {
        const val QUERY = "text"
        const val PAGE = 1
    }
}

fun <T> any(): T = Mockito.any<T>()