package com.example.imagegallery.data.repository

import com.example.imagegallery.FileReader
import com.example.imagegallery.data.datasource.PhotoDataSource
import com.example.imagegallery.data.model.PhotoResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PhotoDataRepositoryImplTest {

    @Mock
    lateinit var dataSource: PhotoDataSource

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private lateinit var subject: PhotoDataRepositoryImpl

    @Before
    fun setUp() {
        subject = PhotoDataRepositoryImpl(dataSource, testCoroutineDispatcher)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testGetPhotos() = testCoroutineDispatcher.runBlockingTest {
        val photoResponse = Gson().fromJson(
            FileReader.readStringFromFile("json/success.json"),
            PhotoResponse::class.java
        )
        `when`(dataSource.getPhotos(anyString(), anyInt())).thenReturn(photoResponse.photos)
        val photos = subject.getPhotos("", 1)
        assertNotNull(photos)
        assertEquals(1, photos.page)
        assertTrue(photos.photo.isNotEmpty())
    }

    @Test(expected = NullPointerException::class)
    fun testSearchPhotosThrowException() = testCoroutineDispatcher.runBlockingTest {
        `when`(dataSource.getPhotos(anyString(), anyInt())).thenThrow(NullPointerException::class.java)
        subject.getPhotos("", 1)
    }
}
